package faang.school.urlshortenerservice.aspect.validation;

import faang.school.urlshortenerservice.annotation.validation.ValidateBindingResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class BindResultAspect {
    @SuppressWarnings("unused")
    @Around(value = "@annotation(validateBindingResult)", argNames = "joinPoint, validateBindingResult")
    public Object checkBindingResult(ProceedingJoinPoint joinPoint, ValidateBindingResult validateBindingResult)
            throws Throwable {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof BindingResult bindingResult && bindingResult.hasErrors()) {
                throw new BindException(bindingResult);
            }
        }
        return joinPoint.proceed();
    }
}
