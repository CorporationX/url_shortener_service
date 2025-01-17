package faang.school.urlshortenerservice.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionHandlingAspect {

    @AfterThrowing(pointcut = "execution(* faang.school.urlshortenerservice.service..*(..))", throwing = "ex")
    public void handleServiceException(Exception ex) throws Exception {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
        throw ex;
    }
}