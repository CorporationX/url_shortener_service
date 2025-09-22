package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.scheduler.shorter.ShorterCleanConfig;
import faang.school.urlshortenerservice.scheduler.shorter.ShorterCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final ShorterCleaner shorterCleaner;
    private final ShorterCleanConfig shorterCleanerConfig;

    @Scheduled(cron = "${scheduler.expired-urls-clean.cron}", zone = "${scheduler.expired-urls-clean.zone}")
    public void clearExpiredUrls() {
        long start = System.currentTimeMillis();
        log.info("Scheduled job started: clearing expired urls...");
        runCleanupJob();
        log.info("Scheduled job finished in {} millis: expired urls cleared.", (System.currentTimeMillis() - start));
    }

    private void runCleanupJob() {
        int attempt = 0;
        int maxAttempts = shorterCleanerConfig.getFetchLimit() / shorterCleanerConfig.getBatchSize();

        while (attempt < maxAttempts) {
            attempt++;
            try {
                shorterCleaner.cleanExpiredUrlsBatchAsync(shorterCleanerConfig.getBatchSize());
            } catch (Exception e) {
                log.error("Clear batch failed", e);
                break;
            }

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
    }
}