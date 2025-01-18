package faang.school.urlshortenerservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CleanerSchedulerLoggingAspect {

    @Before("execution(* faang.school.urlshortenerservice.scheduled.CleanerScheduler.cleanOldUrlsAndSaveHashes(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Started cleaning old URLs and saving hashes...");
    }

    @After("execution(* faang.school.urlshortenerservice.scheduled.CleanerScheduler.cleanOldUrlsAndSaveHashes(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Successfully completed cleaning old URLs and saving hashes.");
    }

    @AfterThrowing(pointcut = "execution(* faang.school.urlshortenerservice.scheduled.CleanerScheduler.cleanOldUrlsAndSaveHashes(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        log.error("Error occurred while cleaning old URLs and saving hashes: ", ex);
    }
}
