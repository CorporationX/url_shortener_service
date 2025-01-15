package faang.school.urlshortenerservice.validate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@Slf4j
public class UrlValidate {

    public URL getValidUrl(String originalUrl) {
        try {
            URL url = new URL(originalUrl);
            if (url.getProtocol().equals("http")) {
                log.warn("Warning! Specified address uses an insecure data transfer protocol");
            }
            return url;
        } catch (MalformedURLException e) {
            log.warn("Address specified in the request is not valid");
            throw new RuntimeException(e);
        }
    }
}
