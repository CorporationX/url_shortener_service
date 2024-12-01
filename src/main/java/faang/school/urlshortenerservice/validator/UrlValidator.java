package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exeption.validation.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class UrlValidator {

    private static final String URL_REGEX = "^(https?|ftp)://[^\s/$.?#].[^\s]*$";
    private final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public void validateUrl(String url) {
        if (!URL_PATTERN.matcher(url).matches()) {
            log.error("URL has to fit into pattern: {} ", URL_REGEX);
            throw new DataValidationException("Invalid URL: " + url);
        }
    }
}
