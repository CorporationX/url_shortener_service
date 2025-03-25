package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UrlValidator {

    private static final String URL_REGEX =
            "^(https?|ftp)://[a-zA-Z0-9.-]+(:[0-9]{1,5})?(/.*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public boolean isValidURL(String url) {
        return URL_PATTERN.matcher(url).matches();
    }
}
