package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.short_url.ShortUrlProperties;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.service.cache.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShortUrlRequestStatsScheduler {

    private final UrlService urlService;
    private final UrlCacheRepository urlCacheRepository;
    private final ShortUrlProperties shortUrlProperties;

    @Scheduled(cron = "${short-url.cache-settings.reset-short-url-request-stats-cron}")
    public void updatePopularShortUrls() {
        log.info("Starting to update cache for popular short URLs.");
        Set<String> popularUrlHashes = urlCacheRepository.getPopularUrlHashes();
        urlCacheRepository.resetShortUrlRequestStats();
        List<Url> urlEntities = urlService.findUrlEntities(popularUrlHashes);
        urlEntities.forEach(url -> urlCacheRepository.save(
                url.getHash(),
                url.getUrl(),
                shortUrlProperties.getCacheSettings().getPopularTtlHours(),
                TimeUnit.HOURS)
        );
        log.info("Finished updating cache for popular short URLs.");
    }
}
