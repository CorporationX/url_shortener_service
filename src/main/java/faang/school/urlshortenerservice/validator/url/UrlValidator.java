package faang.school.urlshortenerservice.validator.url;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<UrlConstraint, String> {
    private static final String regexPattern = "^(http|https)://.*$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(regexPattern);
    }
}
