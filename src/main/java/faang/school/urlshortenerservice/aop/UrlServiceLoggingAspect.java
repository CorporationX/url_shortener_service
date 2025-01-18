package faang.school.urlshortenerservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UrlServiceLoggingAspect {

    @Pointcut("execution(* faang.school.urlshortenerservice.service.UrlService.*(..))")
    public void urlServiceMethods() {
    }

    @Before("urlServiceMethods()")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        log.info("Started execution of method: {}", joinPoint.getSignature().getName());
        log.debug("Arguments: {}", (Object) joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "urlServiceMethods()", returning = "result")
    public void logAfterMethodExecution(JoinPoint joinPoint, Object result) {
        log.info("Successfully executed method: {}", joinPoint.getSignature().getName());
        if (result != null) {
            log.debug("Return value: {}", result);
        }
    }

    @AfterThrowing(pointcut = "urlServiceMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method: {} with message: {}", joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }
}
