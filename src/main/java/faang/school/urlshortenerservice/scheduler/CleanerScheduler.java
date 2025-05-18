package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;

import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Transactional
    @Scheduled(cron = "${cleaner.scheduler.cron}")
    public void cleanOldUrls() {
        try {
            log.info("Starting URL cleanup process");
            List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes();

            if (!hashes.isEmpty()) {
                log.info("Found {} old URLs to clean up", hashes.size());
                hashRepository.save(hashes);
                log.info("Successfully saved {} hashes to hash repository", hashes.size());
            } else {
                log.info("No old URLs found for cleanup");
            }
        } catch (Exception e) {
            log.error("Error during URL cleanup process", e);
            throw new RuntimeException("Failed to clean old URLs", e);
        }
    }
}
