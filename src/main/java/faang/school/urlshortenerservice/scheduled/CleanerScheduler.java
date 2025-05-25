package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.config.scheduler.CleanerScheduleProperties;
import faang.school.urlshortenerservice.entity.Hash;
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
    @Scheduled(cron = "#{@cleanerScheduleProperties.cron}")
    public void cleanOldUrls() {
        if (!scheduleProperties.isEnabled()) {
            log.info("CleanerScheduler is disabled via config");
            return;
        }
        log.info("Starting cleaning old URLs job...");

        Instant oneYearAgo = Instant.now().minus(1, ChronoUnit.YEARS);

        List<String> oldHashes = urlRepository.deleteByCreatedAtBeforeReturningHashes(oneYearAgo);

        if (!oldHashes.isEmpty()) {
            log.info("Returning {} hashes back to hash table", oldHashes.size());

            List<Hash> hashes = oldHashes.stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashes);
        } else {
            log.info("No old URLs to clean");
        }

        log.info("Cleaning job finished");
    }
}

