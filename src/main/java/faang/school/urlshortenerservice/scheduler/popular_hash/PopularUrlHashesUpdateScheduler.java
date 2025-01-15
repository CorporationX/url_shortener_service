package faang.school.urlshortenerservice.scheduler.popular_hash;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.short_url.UrlCacheProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularUrlHashesUpdateScheduler {

    private final UrlService urlService;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlCacheProperties urlCacheProperties;
    private final RedissonClient redissonClient;

    @Value("${scheduler.update-popular-url-hashes.rlock-name}")
    private String rLockName;

    @Async("urlHashTaskExecutor")
    @Scheduled(cron = "${scheduler.update-popular-url-hashes.cron}")
    public void updatePopularShortUrls() {
        RLock lock = redissonClient.getLock(rLockName);
        try {
            if (lock.tryLock(0, 10, TimeUnit.MINUTES)) {
                log.info("Lock acquired, starting to update cache for popular short URLs.");
                Set<String> popularUrlHashes = urlCacheRepository.getPopularUrlHashes();
                urlCacheRepository.resetShortUrlRequestStats();
                List<Url> urlEntities = urlService.findUrlEntities(popularUrlHashes);
                urlEntities.forEach(url -> urlCacheRepository.save(
                        url.getHash(),
                        url.getUrl(),
                        urlCacheProperties.getPopularTtlHours(),
                        TimeUnit.HOURS)
                );
                log.info("Finished updating cache for popular short URLs.");
            } else {
                log.info("Another instance is already processing the popular URL hashes update.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error acquiring lock", e);
        } finally {
            lock.unlock();
        }
    }
}
