package faang.school.urlshortenerservice.annotation;

import faang.school.urlshortenerservice.validator.ValidUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUrlValidator.class)
public @interface ValidUrl {
    String message() default "Некорректный URL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
