package faang.school.urlshortenerservice.annotations;

import faang.school.urlshortenerservice.service.validator.CustomUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CustomUrlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {
    String message() default "Invalid URL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
