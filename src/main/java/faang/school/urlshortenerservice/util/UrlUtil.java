package faang.school.urlshortenerservice.util;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UrlUtil {

    private static final String DEFAULT_HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final UrlValidator APACHE_URL_VALIDATOR = new UrlValidator();

    @Value("${short-url.base-path}")
    private String endpointBasePath;

    public boolean isValidUrl(String url) {
        return APACHE_URL_VALIDATOR.isValid(url);
    }

    public String ensureUrlHasProtocol(String url) {
        return !url.startsWith(DEFAULT_HTTP_PROTOCOL) && !url.startsWith(HTTPS_PROTOCOL)
                ? DEFAULT_HTTP_PROTOCOL + url
                : url;
    }

    public String buildShortUrlFromContext(String hash) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("%s/{hash}".formatted(endpointBasePath))
                .buildAndExpand(hash)
                .toUriString();
    }
}
