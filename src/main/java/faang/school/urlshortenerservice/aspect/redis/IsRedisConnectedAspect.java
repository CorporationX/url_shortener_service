package faang.school.urlshortenerservice.aspect.redis;

import faang.school.urlshortenerservice.annotation.redis.IsRedisConnected;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class IsRedisConnectedAspect {
    @Around(value = "@annotation(isRedisConnected)", argNames = "joinPoint, isRedisConnected")
    public Object isRedisConnected(ProceedingJoinPoint joinPoint, IsRedisConnected isRedisConnected) {
        try {
            return joinPoint.proceed();
        } catch (Throwable exception) {
            log.error("{}", exception.getMessage(), exception);
            return null;
        }
    }
}
