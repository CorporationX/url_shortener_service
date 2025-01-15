package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class SecureUrlValidator {

//    private static final Pattern SAFE_URL_PATTERN = Pattern.compile(
//            "^[a-zA-Z0-9._~:/?#@!$&'()*+,;=%-]*$"
//    );

    public void validate(String url) {
        if (url == null || url.isBlank()){
            log.error("Url can't be blank or empty");
            throw new IllegalArgumentException("Url can't be blank");
        }

        UrlValidator secureUrlValidator = new UrlValidator(new String[]{"http", "https"});
        if (!secureUrlValidator.isValid(url)){
            log.error("Invalid url: {}", url);
            throw new IllegalArgumentException("Invalid url");
        }

//        if(SAFE_URL_PATTERN.matcher(url).matches()){
//            log.error("Invalid url: {}", url);
//            throw new IllegalArgumentException("Invalid url");
//        }
    }
}
