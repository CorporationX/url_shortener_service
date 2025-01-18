package faang.school.urlshortenerservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class RepositoryLoggingAspect {

    @Around("Pointcuts.repositoryMethodsPointcut()")
    public Object aroundAllRepositoryMethodsAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String methodName = methodSignature.getName();
        String className = methodSignature.getDeclaringTypeName();
        Object[] args = proceedingJoinPoint.getArgs();

        log.info("Starting method execution: {}.{} with arguments: {}", className, methodName, args);

        try {
            Object targetMethodResult = proceedingJoinPoint.proceed();
            log.info("Method executed successfully: {}.{}, result: {}", className, methodName, targetMethodResult);
            return targetMethodResult;
        } catch (Throwable ex){
            log.error("Method execution failed: {}.{} with exception: {}", className, methodName, ex.getMessage());
            throw ex;
        }
    }
}
