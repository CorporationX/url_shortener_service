package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UrlValidatorImpl implements UrlValidator {

    @Value("${validator.hashPattern}")
    private String hashPattern;
    @Value("${validator.urlPattern}")
    private String urlPattern;

    @Override
    public void validateHash(String hash) {
        boolean isHashValid = Pattern
                .compile(hashPattern)
                .matcher(hash)
                .find();
        if (!isHashValid) {
            throw new DataValidationException("Invalid url");
        }
    }

    @Override
    public void validateUrl(String url) {
        boolean isUrlValid = Pattern
                .compile(urlPattern)
                .matcher(url)
                .find();
        if (!isUrlValid) {
            throw new DataValidationException("Invalid url");
        }
    }

}
