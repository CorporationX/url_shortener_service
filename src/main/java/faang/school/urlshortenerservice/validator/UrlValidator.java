package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;

@Component
public class UrlValidator {
    public void validate(String url) {
        if (url == null || url.isBlank()){
            throw new IllegalArgumentException("Url can't be blank");
        }
    }
}
