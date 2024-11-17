package faang.school.urlshortenerservice.service.validator;

import faang.school.urlshortenerservice.annotations.ValidUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

public class CustomUrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("URL is empty").addConstraintViolation();
            return false;
        }
        try {
            URL url = new URL(value);
            if (url.getHost() == null || url.getHost().isEmpty()) {
                return false;
            }
            InetAddress address = InetAddress.getByName(url.getHost());
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
