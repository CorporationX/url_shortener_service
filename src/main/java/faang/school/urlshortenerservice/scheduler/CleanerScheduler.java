package faang.school.urlshortenerservice.scheduler;


import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j

public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${cleaner.schedule.cron}")
    @Transactional
    public void cleanUpOldUrls() {
        log.info("Cleaning up old urls and hashes");

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        int batchSize = 1000;
        int deletedCount = 0;

        List<String> freedHashes;
        do {
            freedHashes = urlRepository.deleteByCreatedAtBefore(oneYearAgo);
            if (!freedHashes.isEmpty()) {
                log.info("Deleted {} old URLs. Reclaiming {} hashes...", freedHashes.size(), freedHashes.size());
                hashRepository.saveHashes(freedHashes);
                deletedCount += freedHashes.size();
            }
        } while (!freedHashes.isEmpty());

        log.info("Cleanup completed. Total URLs deleted: {}", deletedCount);
    }
}