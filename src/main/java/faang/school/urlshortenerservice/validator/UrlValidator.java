package faang.school.urlshortenerservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidator implements ConstraintValidator<UrlConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            URL url = new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
