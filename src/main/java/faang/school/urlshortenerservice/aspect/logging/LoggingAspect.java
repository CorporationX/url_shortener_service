package faang.school.urlshortenerservice.aspect.logging;

import faang.school.urlshortenerservice.annotation.logging.LogExecution;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @SuppressWarnings("unused")
    @Around(value = "@annotation(logExecution)", argNames = "joinPoint, logExecution")
    public Object logExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        try {
            return joinPoint.proceed();
        } catch (Exception exception) {
            log.error("Exception in method {}: {}", methodName, exception.getMessage(), exception);
            throw exception;
        }
    }
}
