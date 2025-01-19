package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.config.annotation.ValidURL;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlValidator implements ConstraintValidator<ValidURL, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            new URL(value).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException exception) {
            return false;
        }
    }
}
