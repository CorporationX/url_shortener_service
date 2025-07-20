package faang.school.urlshortenerservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
@Order(0)
public class AsyncLoggingAspect {

    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    public Object logAsyncMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        log.info("[ASYNC START] {}", methodName);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            if (result instanceof CompletableFuture<?> future) {
                return future
                        .whenComplete((res, ex) -> {
                            long duration = System.currentTimeMillis() - start;
                            if (ex == null) {
                                logSuccess(methodName, start);
                            } else {
                                log.error("[ASYNC FAILED] {} — {} ms", methodName, duration, ex);
                            }
                        });
            } else {
                logSuccess(methodName, start);
                return result;
            }

        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("[ASYNC EXCEPTION] {} — {} ms", methodName, duration, ex);
            throw ex;
        }
    }

    private void logSuccess(String methodName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        log.info("[ASYNC SUCCESS] {} — {} ms", methodName, duration);
    }
}
