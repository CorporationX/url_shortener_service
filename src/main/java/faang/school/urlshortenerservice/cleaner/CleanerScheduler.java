package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    @Scheduled(cron = "${spring.cleaner.cron}")
    public void cleanAndStoreHashes() {
        try {
            List<String> hashes = urlRepository.removeOldUrlsToHash();
            log.info("Removed old urls from hashes: {}", hashes);
            hashes.forEach(urlCacheRepository::deleteUrl);
            log.info("Old urls removed");
        } catch (Exception e) {
            log.error("Error when cleaning:", e);
        }
    }
}
