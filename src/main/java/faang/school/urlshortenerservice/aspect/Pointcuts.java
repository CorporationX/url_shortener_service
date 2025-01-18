package faang.school.urlshortenerservice.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Pointcuts {

    @Pointcut("execution(* faang.school.urlshortenerservice.repository.*.*(..))")
    public void repositoryMethodsPointcut() {
    }
}
