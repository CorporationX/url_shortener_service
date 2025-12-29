package faang.school.urlshortenerservice.aspect;

import faang.school.urlshortenerservice.annotation.RateLimited;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exception.RateLimitExceededException;
import faang.school.urlshortenerservice.service.MetricsService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final ProxyManager<String> proxyManager;
    private final UserContext userContext;
    private final MetricsService metricsService;

    @Value("${rate-limit.capacity:10}")
    private long capacity;

    @Value("${rate-limit.refill-tokens:10}")
    private long refillTokens;

    @Value("${rate-limit.refill-duration-seconds:60}")
    private long refillDurationSeconds;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = getUserKey();
        
        long effectiveCapacity = rateLimited.capacity() > 0 ? rateLimited.capacity() : capacity;
        long effectiveRefillTokens = rateLimited.refillTokens() > 0 ? rateLimited.refillTokens() : refillTokens;
        long effectiveRefillDuration = rateLimited.refillDurationSeconds() > 0 
                ? rateLimited.refillDurationSeconds() 
                : refillDurationSeconds;
        
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(
                        effectiveCapacity,
                        Refill.intervally(
                                effectiveRefillTokens,
                                Duration.ofSeconds(effectiveRefillDuration)
                        )
                ))
                .build();

        Bucket bucket = proxyManager.builder()
                .build(key, () -> configuration);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for key: {}", key);
            metricsService.rateLimitExceededCounter().increment();
            throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
        }

        return joinPoint.proceed();
    }

    private String getUserKey() {
        Long userId = userContext.getUserId();
        if (userId != null && userId > 0) {
            return "rate-limit:user:" + userId;
        }
        return "rate-limit:anonymous";
    }
}

