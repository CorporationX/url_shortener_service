package faang.school.urlshortenerservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlConstraint {

    String message() default "Invalid account number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
