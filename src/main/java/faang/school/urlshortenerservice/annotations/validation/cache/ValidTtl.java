package faang.school.urlshortenerservice.annotations.validation.cache;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {TtlValidator.class})
public @interface ValidTtl {
    String message() default "When TTL is on the TTL value cannot be null or negative";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
