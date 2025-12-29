package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.properties.HashGeneratorProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashGeneratorProperties properties;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final MetricsService metricsService;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> generateHashBatch() {
        return CompletableFuture.runAsync(() -> {
            Timer.Sample sample = Timer.start();
            try {
                RetryPolicy<Void> retryPolicy = new RetryPolicy<Void>()
                        .handle(Exception.class)
                        .withDelay(Duration.ofMillis(properties.getRetry().getDelayMs()))
                        .withMaxRetries(properties.getRetry().getMaxAttempts() - 1)
                        .onRetry(e -> {
                            if (e.getLastFailure() != null) {
                                log.warn("Retry hash generation attempt. Error: {}", e.getLastFailure().getMessage());
                            }
                        })
                        .onFailure(e ->
                                log.error("Hash generation failed after all attempts", e.getFailure())
                        );

                Failsafe.with(retryPolicy).run(this::doGenerateBatch);
            } finally {
                sample.stop(metricsService.hashGenerationDurationTimer());
            }
        }).exceptionally(throwable -> {
            metricsService.hashGenerationErrorCounter(throwable.getClass().getSimpleName()).increment();
            log.error("Hash generation failed with unhandled exception", throwable);
            return null;
        });
    }

    private void doGenerateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(properties.getBatchSize());

        if (uniqueNumbers == null || uniqueNumbers.isEmpty()) {
            log.debug("No unique numbers available for hash generation");
            return;
        }

        List<String> hashes = encoder.encode(uniqueNumbers);

        if (hashes == null || hashes.isEmpty()) {
            throw new RuntimeException("Encoder produced no hashes for input of size: " + uniqueNumbers.size());
        }

        if (hashes.size() != uniqueNumbers.size()) {
            throw new RuntimeException(
                    String.format("Hash count mismatch: expected %d, got %d", uniqueNumbers.size(), hashes.size())
            );
        }

        hashRepository.save(hashes);
        
        log.info("Successfully generated and saved {} hashes", hashes.size());
        metricsService.hashGenerationSuccessCounter(hashes.size()).increment();
    }

}
