package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${spring.crontab.deleteOldHashes}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting scheduled job to clean old URLs...");
        try {
            var expiredHashes = urlRepository.deleteOldUrlsAndReturnHashes();
            log.info("Deleted {} old URLs", expiredHashes.size());

            hashRepository.saveAllHashes(expiredHashes);
            log.info("Saved {} hashes back to the hash table", expiredHashes.size());
        } catch (Exception e) {
            log.error("Error occurred during the cleaning job", e);
            throw e;
        }
        log.info("Scheduled job completed successfully.");
    }
}