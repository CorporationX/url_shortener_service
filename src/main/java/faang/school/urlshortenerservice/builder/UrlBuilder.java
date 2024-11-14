package faang.school.urlshortenerservice.builder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlBuilder {
    @Value("${server.url}")
    private String serverUrl;

    public String makeUrl(String hash) {
        return String.format("%s/%s", serverUrl, hash);
    }
}
