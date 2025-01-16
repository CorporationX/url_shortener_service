package faang.school.urlshortenerservice.aspect;



import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class CachingAspect {

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    @Around("execution(* faang.school.urlshortenerservice.service.UrlService.getLongUrl(..)) && args(hash)")
    public Object cacheGetLongUrl(ProceedingJoinPoint joinPoint, String hash) throws Throwable {

        if (cache.containsKey(hash)) {
            log.info("Cache hit for hash: {}", hash);
            return cache.get(hash);
        }


        log.info("Cache miss for hash: {}", hash);
        Object result = joinPoint.proceed();


        cache.put(hash, (String) result);
        log.info("Result cached for hash: {}", hash);

        return result;
    }
}
