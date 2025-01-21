package faang.school.urlshortenerservice.validate;


import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
@Slf4j
@Component
public class UrlValidate {

    public URL getUrlValidate(UrlDto originalUrl) {
        try {
            URL url = new URL(originalUrl.getOriginalUrl());
            if (url.getProtocol().equals("http")) {
            }
            return url;
        } catch (MalformedURLException e) {
            log.warn("Address not have <http>");
            throw new RuntimeException(e);
        }
    }
}
