package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import faang.school.urlshortenerservice.repository.interfaces.UrlRepository;
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

    @Scheduled(cron = "${app.scheduler.cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting cleanup of old URLs");

        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        List<String> expiredHashes = urlRepository.deleteOlderThan(threshold);

        if (expiredHashes.isEmpty()) {
            log.info("No expired URLs found to clean");
            return;
        }

        log.info("Deleted {} expired URLs, returning hashes to hash table", expiredHashes.size());

        hashRepository.save(expiredHashes);

        log.info("Successfully returned {} hashes to hash table", expiredHashes.size());
    }
}
