package faang.school.urlshortenerservice.validator;

import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static faang.school.urlshortenerservice.encoder.Base62Encoder.BASE_62_CHARACTERS;

@Component
public class UrlServiceValidator {
    private final Pattern pattern = Pattern.compile("[^" + BASE_62_CHARACTERS + "]");

    public void validateHash(String hash) {
        if (hash.length() > 6 || StringUtils.isBlank(hash) || pattern.matcher(hash).find()) {
            throw new ValidationException("Invalid hash %s".formatted(hash));
        }
    }
}
