package baum.urlshortenerservice.validator;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Aspect
@Component
@RequiredArgsConstructor
public class MethodParamValidationAspect {
    private final ParamValidator validator;

    @Before(value = "@annotation(baum.urlshortenerservice.validator.annotaiton.ValidParams)")
    public void validateParameters(JoinPoint joinPoint) {
        Stream.of(joinPoint.getArgs()).forEach(validator::validate);
    }
}