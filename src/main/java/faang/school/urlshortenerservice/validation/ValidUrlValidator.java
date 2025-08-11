package faang.school.urlshortenerservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;

public class ValidUrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Let @NotBlank handle empty validation
        }

        try {
            URI uri = new URI(value);
            return uri.getScheme() != null &&
                  (uri.getScheme().equals("http") || uri.getScheme().equals("https"));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}