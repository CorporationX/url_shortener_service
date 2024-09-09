package faang.school.urlshortenerservice.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Evgenii Malkov
 */
@Component
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Value("${service.base-prefix}")
    private String basePrefix;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        if (!value.startsWith(basePrefix)) {
            return false;
        }

        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
