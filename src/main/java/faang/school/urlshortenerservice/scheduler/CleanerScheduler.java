package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.CleanerProperties;
import faang.school.urlshortenerservice.exception.CleanupException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.CleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final CleanupService cleanupService;
    private final CleanerProperties cleanerProps;

    @Scheduled(cron = "${scheduler.cleaner.cron}")
    @SchedulerLock(
            name = "${scheduler.cleaner.lock-name}",
            lockAtLeastFor = "${scheduler.cleaner.lock-at-least-for}",
            lockAtMostFor = "${scheduler.cleaner.lock-at-most-for}"
    )
    public void cleanUp() {
        log.info("Starting cleanup job for URLs older than {} days", cleanerProps.expiryDate());

        try {
            LocalDateTime expiryDate = LocalDateTime.now().minusDays(cleanerProps.expiryDate());

            long totalExpired = urlRepository.countExpiredUrls(expiryDate);
            if (totalExpired == 0) {
                log.info("No expired URLs found");
                return;
            }

            log.info("Found {} expired URLs to clean up. Processing in batches of {}",
                    totalExpired, cleanerProps.batchSize());

            long totalProcessed = processBatchCleanup(expiryDate);

            log.info("Cleanup job completed successfully. Processed {} URLs", totalProcessed);

        } catch (Exception e) {
            log.error("Error during cleanup job execution", e);
            throw new CleanupException("Failed to execute cleanup job", e);
        }
    }

    private long processBatchCleanup(LocalDateTime cutoffDate) {
        long totalProcessed = 0;
        int batchCount = 0;
        while (batchCount < cleanerProps.maxBatches()) {
            try {
                List<String> batchHashes = cleanupService.deleteExpiredBatch(
                        cutoffDate, cleanerProps.batchSize());

                if (batchHashes.isEmpty()) {
                    log.info("No more expired URLs to process. Cleanup complete.");
                    break;
                }

                cleanupService.returnHashesToPool(batchHashes);
                totalProcessed += batchHashes.size();
                batchCount++;

                log.info("Processed batch {}/{}: {} URLs (total: {})",
                        batchCount, cleanerProps.maxBatches(), batchHashes.size(), totalProcessed);

            } catch (Exception e) {
                log.error("Error processing batch {}", batchCount + 1, e);
                batchCount++;
            }
        }
        return totalProcessed;
    }
}