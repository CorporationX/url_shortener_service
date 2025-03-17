package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UriBuilder {
    public String response(String hash) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{hash}")
                .buildAndExpand(hash)
                .toUriString();
    }
}