package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "url.base")
@Getter
@Setter
public class ShortUrlProperties {

    private String domain;
    private String scheme;

    public String getBaseUrl() {
        return String.format("%s://%s", scheme, domain);
    }
}
