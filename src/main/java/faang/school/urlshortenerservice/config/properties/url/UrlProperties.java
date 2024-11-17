package faang.school.urlshortenerservice.config.properties.url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "url")
public class UrlProperties {

    private UrlShort urlShort;

    @Getter
    @Setter
    public static class UrlShort {

        private String baseUrl;
        private String urlRegex;
    }
}
