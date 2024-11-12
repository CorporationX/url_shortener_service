package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.annotation.Url;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class UrlValidator implements ConstraintValidator<Url, String> {

    @Value("${server.regex.url}")
    private String urlPattern;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Pattern.matches(urlPattern, value);
    }
}

