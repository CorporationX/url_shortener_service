package faang.school.urlshortenerservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class HashCacheLoggingAspect {

    @Pointcut("execution(* faang.school.urlshortenerservice.utils.HashCache.*(..))")
    public void hashCacheMethods() {
    }

    @Before("hashCacheMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Started execution of method: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "hashCacheMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Completed execution of method: {}", joinPoint.getSignature().getName());
        if (result != null) {
            log.debug("Method returned: {}", result);
        }
    }

    @AfterThrowing(pointcut = "hashCacheMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method: {} with message: {}", joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }
}
