package faang.school.urlshortenerservice.aop;

import faang.school.urlshortenerservice.service.cache.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UrlAspect {

    private final UrlCacheRepository urlCacheRepository;

    @AfterReturning(value = "@annotation(org.springframework.cache.annotation.Cacheable) && args(hash)")
    public void updateRequestStatsAfterFindingOriginalUrl(String hash) {
        urlCacheRepository.updateShortUrlRequestStats(hash);
    }
}
