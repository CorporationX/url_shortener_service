package faang.school.urlshortenerservice.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UriUtils {
    public static URI converToUri(String stringUrl) {
        try {
            URI uri = new URL(stringUrl).toURI();
            return uri;
        } catch (URISyntaxException | MalformedURLException e) {
            log.error(stringUrl + " is not a valid url", e);
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}
