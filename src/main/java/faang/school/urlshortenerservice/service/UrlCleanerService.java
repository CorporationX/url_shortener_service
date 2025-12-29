package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCleanerService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleaner.retention-years:1}")
    private int retentionYears;

    @Value("${cleaner.batch-size:1000}")
    private int batchSize;

    @Value("${cleaner.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${cleaner.retry.delay-ms:1000}")
    private long retryDelayMs;

    private volatile boolean shuttingDown = false;

    @PreDestroy
    public void onShutdown() {
        log.warn("Shutdown signal received, stopping cleanup gracefully...");
        shuttingDown = true;
    }

    public void cleanOldUrls() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(retentionYears);
        log.info("Starting cleanup of URLs older than {}", cutoffDate);

        int totalProcessed = 0;

        while (!shuttingDown) {
            List<String> batchHashes = urlRepository.getOldUrlHashesBatch(cutoffDate, batchSize);

            if (batchHashes == null || batchHashes.isEmpty()) {
                log.info("No more old URLs found to clean");
                break;
            }

            log.info("Processing batch of {} URLs (total processed: {})", batchHashes.size(), totalProcessed);

            if (shuttingDown) {
                log.warn("Shutdown in progress, stopping before processing batch");
                break;
            }

            try {
                int processed = processBatchWithRetry(batchHashes);
                totalProcessed += processed;

                if (shuttingDown) {
                    log.warn("Shutdown in progress, stopping at {} processed URLs", totalProcessed);
                    break;
                }
            } catch (Exception e) {
                log.error("Failed to process batch after retries, stopping cleanup", e);
                throw e;
            }
        }

        log.info("Cleanup stopped (gracefully): {} URLs processed", totalProcessed);
    }

    private int processBatchWithRetry(List<String> hashes) {
        RetryPolicy<Integer> retryPolicy = new RetryPolicy<Integer>()
                .handle(Exception.class)
                .withDelay(Duration.ofMillis(retryDelayMs))
                .withMaxRetries(maxRetryAttempts - 1)
                .onRetry(e -> {
                    if (e.getLastFailure() != null) {
                        log.warn("Retry processing batch attempt. Error: {}", e.getLastFailure().getMessage());
                    }
                })
                .onFailure(e -> {
                    log.error("Batch processing failed after all retry attempts", e.getFailure());
                });

        return Failsafe.with(retryPolicy).get(() -> processBatch(hashes));
    }

    @Transactional
    protected int processBatch(List<String> hashes) {
        if (shuttingDown) {
            log.warn("Shutdown detected during batch processing, aborting");
            return 0;
        }

        log.debug("Saga Step 2: Saving {} hashes to hash table", hashes.size());
        hashRepository.save(hashes);

        if (shuttingDown) {
            log.warn("Shutdown detected after saving hashes, compensating");
            compensateSaveHashes(hashes);
            return 0;
        }

        log.debug("Saga Step 3: Deleting {} old URLs", hashes.size());
        urlRepository.deleteUrlsByHashes(hashes);

        log.debug("Successfully processed batch of {} URLs", hashes.size());
        return hashes.size();
    }

    @Transactional
    protected void compensateSaveHashes(List<String> hashes) {
        log.warn("Compensating: Removing {} hashes from hash table", hashes.size());
        try {
            hashRepository.delete(hashes);
            log.info("Compensation completed: removed {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Compensation failed: could not remove hashes", e);
        }
    }
}