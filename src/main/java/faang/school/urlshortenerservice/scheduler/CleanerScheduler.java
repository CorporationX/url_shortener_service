package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting cleanup job for old URLs");

        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);

        try {
            List<String> releasedHashes = urlRepository.deleteOldUrlsAndReturnHashes(cutoffDate);

            if (!releasedHashes.isEmpty()) {
                hashRepository.save(releasedHashes);

                log.info("Cleanup job completed successfully. Deleted {} old URLs and returned their hashes to the pool",
                        releasedHashes.size());
            } else {
                log.info("No old URLs found for cleanup");
            }

        } catch (Exception e) {
            log.error("Error occurred during cleanup job", e);
            throw e;
        }
    }
}
