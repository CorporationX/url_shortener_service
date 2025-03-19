package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UrlValidator {

    public URL validateUrl(UrlDto dto) {
        try {
            return new URL(dto.getUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
