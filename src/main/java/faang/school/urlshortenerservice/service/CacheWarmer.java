package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheWarmer {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;

    @Value("${app.cache_warmer.enabled}")
    private boolean enabled;

    @Value("${app.cache_warmer.top_urls}")
    private int topUrls;

    @PostConstruct
    public void warmUpCache() {
        if (!enabled) return;

        log.info("Warming up cache with top {} URLs", topUrls);
        List<Url> urls = urlRepository.findTopUrls(topUrls);
        urls.forEach(url -> cacheRepository.save(url.getHash(), url.getUrl()));
        log.info("Cache warm-up completed");
    }
}