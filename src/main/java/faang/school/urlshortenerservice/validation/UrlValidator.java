package faang.school.urlshortenerservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url.isEmpty()) {
            return true;
        }

        return url.startsWith("https://");
    }
}
