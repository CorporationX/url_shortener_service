package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.exception.UrlCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UrlBuilder {

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int port;

    @Value("${url.endpoint}")
    private String endPoint;

    public URL createShortUrl(String hash) {
        try {
            return UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host(host)
                    .port(port)
                    .pathSegment(endPoint, hash)
                    .build()
                    .toUri().toURL();
        } catch (MalformedURLException e) {
            throw new UrlCreationException("Failed to create short url");
        }
    }
}
