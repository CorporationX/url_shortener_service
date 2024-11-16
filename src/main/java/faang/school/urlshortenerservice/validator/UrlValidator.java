package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exeption.validation.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class UrlValidator {

    private final String URL_REGEX = "^(https?|ftp)://[^\s/$.?#].[^\s]*$";
    private final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public void isValidUrl(String url) {
        try {
            if (!URL_PATTERN.matcher(url).matches()) {
                throw new DataValidationException("Invalid URL");
            }
        } catch (DataValidationException e) {
            log.error("URL name is not valid: {}", e.getMessage());
            throw new DataValidationException("URL is not valid: " + e.getMessage());
        }
    }
}
