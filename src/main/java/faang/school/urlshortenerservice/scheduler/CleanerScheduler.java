package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.url.CleanerProperties;
import faang.school.urlshortenerservice.repository.cache.UrlCache;
import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlService urlService;
    private final UrlCache urlCache;
    private final CleanerProperties cleanerProperties;

    @Scheduled(cron = "${shortener.cleaner.cron}")
    public void run() {
        LocalDateTime deleteBefore = LocalDateTime.now().minus(cleanerProperties.retention());
        log.info("Cleaner started, deleteBefore = {}", deleteBefore);

        List<String> deletedHashes = urlService.cleanOldUrls(deleteBefore);

        if (deletedHashes.isEmpty()) {
            log.info("Cleaner finished: nothing to delete");
            return;
        }
        try {
            urlCache.delete(deletedHashes);
            log.info("Cleaner finished: deleted {} keys from Redis", deletedHashes.size());
        } catch (Exception e) {
            log.warn("Cleaner: Redis eviction failed", e);
        }
    }
}
