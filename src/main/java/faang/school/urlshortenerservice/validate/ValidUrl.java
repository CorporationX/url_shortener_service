package faang.school.urlshortenerservice.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Evgenii Malkov
 */
@Constraint(validatedBy = UrlValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {
    String message() default "Not correct url format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
