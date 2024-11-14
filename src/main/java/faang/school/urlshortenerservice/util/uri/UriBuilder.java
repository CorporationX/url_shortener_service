package faang.school.urlshortenerservice.util.uri;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UriBuilder {
    @Value("${app.uri}")
    private String appUri;

    public String response(String hash) {
        return appUri + hash;
    }
}
