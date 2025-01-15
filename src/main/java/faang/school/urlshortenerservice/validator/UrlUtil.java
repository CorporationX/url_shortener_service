package faang.school.urlshortenerservice.validator;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

@Component
public class UrlUtil {

    private static final String DEFAULT_HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final UrlValidator APACHE_URL_VALIDATOR = new UrlValidator();

    public boolean isValidUrl(String url) {
        return APACHE_URL_VALIDATOR.isValid(url);
    }

    public String ensureUrlHasProtocol(String url) {
        return !url.startsWith(DEFAULT_HTTP_PROTOCOL) && !url.startsWith(HTTPS_PROTOCOL)
                ? DEFAULT_HTTP_PROTOCOL + url
                : url;
    }
}
