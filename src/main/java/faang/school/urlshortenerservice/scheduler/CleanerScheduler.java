package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.service.cache.UrlRedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CleanerScheduler {
    private final UrlService urlService;
    private final HashService hashService;
    private final UrlRedisCacheService urlRedisCacheService;

    @Scheduled(cron = "${app.scheduler.clear-unused-hashes}")
    @Transactional
    public void cleanUnusedUrl() {
        List<String> hashes = urlService.getAndDeleteUnusedHashes();
        hashService.save(hashes);
        hashes.forEach(urlRedisCacheService::delete);
    }
}
