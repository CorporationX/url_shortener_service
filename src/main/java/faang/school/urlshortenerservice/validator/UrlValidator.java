package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UrlValidator {
    public boolean isValid(UrlDto url) {
        try {
            new URL(url.getUrl());
            return true;
        } catch (MalformedURLException e) {
            throw new DataValidationException("Passed url isn't valid. Please check your url.");
        }
    }
}
