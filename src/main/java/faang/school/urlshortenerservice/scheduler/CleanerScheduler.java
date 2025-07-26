package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.CleanerProperties;
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
    private final CleanupService cleanupService;
    private final CleanerProperties properties;

    @Scheduled(cron = "${scheduler.cleaner.cron}")
    @SchedulerLock(
            name = "${scheduler.cleaner.lock-name}",
            lockAtLeastFor = "${scheduler.cleaner.lock-at-least-for}",
            lockAtMostFor = "${scheduler.cleaner.lock-at-most-for}"
    )
    public void cleanUp() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(properties.expiryDate());
        log.info("Starting cleanup job for URLs older than {} days", expiryDate);

        try {
            processBatchCleanup(expiryDate);
            log.info("Cleanup job completed successfully.");
        } catch (Exception e) {
            log.error("Error during cleanup job execution", e);
        }
    }

    private void processBatchCleanup(LocalDateTime expiryDate) {
        for (int i = 0; i < properties.maxBatches(); i++) {
            List<String> batchHashes = cleanupService.deleteExpiredBatch(
                    expiryDate, properties.batchSize());
            if (batchHashes.isEmpty()) {
                return;
            }
            cleanupService.returnHashesToPool(batchHashes);
        }
    }
}