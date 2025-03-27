package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${clean-expired-url.expiration-days}")
    private int expirationDays;

    @Scheduled(cron = "${clean-expired-url.cron}")
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Running cleanup job for expired URLs...");

        List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes(expirationDays);

        if (!freedHashes.isEmpty()) {
            hashRepository.saveAllHashes(freedHashes);
            log.info("Moved {} freed hashes back to DB.", freedHashes.size());
        } else {
            log.info("No expired URLs found to delete.");
        }
    }
}