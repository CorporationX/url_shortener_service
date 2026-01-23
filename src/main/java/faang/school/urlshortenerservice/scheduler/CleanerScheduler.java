package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.CleanerConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlJdbcRepository urlJdbcRepository;
    private final HashRepository hashRepository;
    private final CleanerConfig cleanerConfig;

    @Scheduled(cron = "${app.cleaner.cron}")
    @Transactional
    public void cleanupOldUrlsAndRecycleHashes() {
        if (!cleanerConfig.isEnabled()) {
            log.info("Cleaner scheduler is disabled");
            return;
        }
        log.info("Starting cleanup  URLs older than {} days and recycling of hashes",
                cleanerConfig.getDeleteOlderThanDays());

        try {
            List<String> recycledHashes = urlJdbcRepository.deleteOldUrlsAndGetHashes(
                    cleanerConfig.getDeleteOlderThanDays());
            log.info("Found and deleted {} old URLs", recycledHashes.size());

            if (!recycledHashes.isEmpty()) {
                hashRepository.save(recycledHashes);
                log.info("Saved {} recycled hashes to database", recycledHashes.size());
            } else {
                log.info("No old URLs found for cleanup");
            }
            log.info("Cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during cleanup: {}", e.getMessage(), e);
        }
    }
}
