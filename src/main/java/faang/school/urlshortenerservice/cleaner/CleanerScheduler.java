package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
            if (hashes == null || hashes.isEmpty()) {
                log.warn("No expired URLs found for removal.");
                return;
            }

            urlCacheRepository.deleteUrl(hashes);
            log.info("Successfully removed {} expired URLs. Hashes: {}", hashes.size(), hashes);

        } catch (Exception e) {
            log.error("Error during scheduled cleaning", e);
        }
    }
}
