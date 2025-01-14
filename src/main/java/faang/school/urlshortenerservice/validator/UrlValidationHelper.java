package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

@Component
public class UrlValidationHelper {

    private static final String DEFAULT_HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final UrlValidator APACHE_URL_VALIDATOR = new UrlValidator();

    public void validateUrl(String url) {
        String urlWithProtocol = getUrlWithDefaultProtocol(url);
        if (!APACHE_URL_VALIDATOR.isValid(urlWithProtocol)) {
            throw new DataValidationException("Incorrect url!");
        }
    }

    private String getUrlWithDefaultProtocol(String url) {
        return !url.startsWith(DEFAULT_HTTP_PROTOCOL) && !url.startsWith(HTTPS_PROTOCOL)
                ? DEFAULT_HTTP_PROTOCOL + url
                : url;
    }
}
