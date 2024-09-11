package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${url.hash.cleaner.cron}")
    public void cleanUpOutdatedUrls() {
        log.info("Clean up of outdated URLs started...");
        var oneYearAgo = LocalDateTime.now().minusYears(1);
        var freedHashes = urlRepository.deleteUrlsAndFreeHashes(oneYearAgo);
        if (!freedHashes.isEmpty()) {
            hashRepository.save(freedHashes);
        }
        log.info("Cleanup completed. Freed up {} hashes from outdated URLs", freedHashes.size());
    }
}
