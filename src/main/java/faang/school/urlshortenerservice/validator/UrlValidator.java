package faang.school.urlshortenerservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<UrlConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        } else {
            String number = value.replaceAll(" ", "");
            return number.length() >= 12 && number.length() <= 20;
        }
    }
}
