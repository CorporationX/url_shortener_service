package faang.school.urlshortenerservice.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UrlStartValidate.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateUrl {
    String message() default "Url must start with http:// or https://";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
