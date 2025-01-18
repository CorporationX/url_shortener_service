package faang.school.urlshortenerservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CacheLoggingAspect {

    @AfterReturning(value = "@annotation(org.springframework.cache.annotation.Cacheable) && args(hash)",
            returning = "result")
    public void logCacheableMethodAfterReturning(String hash, Object result) {
        log.info("Got original Url - {} related to Hash - {}", hash, result);
    }
}
