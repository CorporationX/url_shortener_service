package faang.school.urlshortenerservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* faang.school.urlshortenerservice.service.UrlService.*(..)) || execution(* faang.school.urlshortenerservice.repository..*(..))")
    public void loggingPointcut() {
    }

    @Before("loggingPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Executing method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "loggingPointcut()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        log.info("Exiting method: {} with result: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method: {} with message: {}", joinPoint.getSignature(), exception.getMessage(), exception);
    }
}