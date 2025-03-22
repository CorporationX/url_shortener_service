package faang.school.urlshortenerservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
@Component
public class UrlValidator {

    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try{
            new URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (MalformedURLException e){
            return false;
        }
    }
}
