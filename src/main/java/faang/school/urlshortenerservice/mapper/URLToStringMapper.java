package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.exception.UnValidUrlException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Converter(autoApply = true)
@Component
public class URLToStringMapper implements AttributeConverter<URL, String> {
    @Override
    public String convertToDatabaseColumn(URL url) {
        if (url == null) {
            return null;
        }
        return url.toString();
    }

    @Override
    public URL convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty()) {
            throw new UnValidUrlException("URL-string is null or empty");
        }
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new UnValidUrlException("Invalid URL: %s".formatted(s));
        }
    }
}
