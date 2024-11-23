package faang.school.urlshortenerservice.validation.validator;

import faang.school.urlshortenerservice.annotation.Url;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class UrlValidator implements ConstraintValidator<Url, String> {

    private Pattern pattern;

    @Value("${spring.validation.url-regex}")
    private String urlRegex;

    @Override
    public void initialize(Url constraintAnnotation) {
        pattern = Pattern.compile(urlRegex);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return pattern.matcher(value).matches();
    }
}
