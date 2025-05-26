package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.config.scheduler.CleanerScheduleProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.SchedulerException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final CleanerScheduleProperties scheduleProperties;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${scheduling.url-cleaner.cron}")
    public void cleanOldUrls() {
        if (!scheduleProperties.isEnabled()) {
            log.debug("CleanerScheduler is disabled via config");
            return;
        }

        try {
            log.info("Starting cleaning old URLs job...");

            Instant threshold = Instant.now().minus(scheduleProperties.getRetentionDays(), ChronoUnit.DAYS);
            List<String> oldHashes = urlRepository.deleteByCreatedAtBeforeReturningHashes(threshold);

            if (!oldHashes.isEmpty()) {
                log.info("Returning {} hashes back to hash table", oldHashes.size());

                List<Hash> hashes = oldHashes.stream()
                        .map(Hash::new)
                        .toList();

                hashRepository.saveAll(hashes);
                log.info("Successfully returned {} hashes to the pool", oldHashes.size());
            } else {
                log.info("No old URLs to clean");
            }

            log.info("Cleaning job finished successfully");
        } catch (Exception e) {
            log.error("Error during URL cleaning job", e);
            throw new SchedulerException("Failed to execute URL cleaning job", e);
        }
    }
}

