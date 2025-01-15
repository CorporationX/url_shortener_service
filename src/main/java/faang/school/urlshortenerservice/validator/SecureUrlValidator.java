package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecureUrlValidator {

    public void validate(String url) {
        UrlValidator secureUrlValidator = new UrlValidator(new String[]{"http", "https"});
        if (!secureUrlValidator.isValid(url)){
            log.error("Invalid url: {}", url);
            throw new IllegalArgumentException("Invalid url");
        }
    }
}
