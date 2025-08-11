package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.annotation.ValidUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidUrlValidator implements ConstraintValidator<ValidUrl, String> {

    private static final String URL_REGEX = "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)";
    private static final Pattern PATTERN = Pattern.compile(URL_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            // Сообщаем, что URL пуст
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("URL не должен быть пустым")
                    .addConstraintViolation();
            return false;
        }

        if (!PATTERN.matcher(value).matches()) {
            // Только если не пустой, но невалидный
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("URL должен быть валидным")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
