package faang.school.urlshortenerservice.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlStartValidate implements ConstraintValidator<ValidateUrl, String> {
    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}
