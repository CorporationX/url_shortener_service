package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@NoArgsConstructor
public class UrlValidator {
    public void validateUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new DataValidationException("Please provide a valid URL");
        }
    }
}
