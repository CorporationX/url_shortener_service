package faang.school.urlshortenerservice.builder;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UrlBuilder {

    public String makeUrl(String hash) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{hash}")
                .buildAndExpand(hash)
                .toUriString();
    }
}
