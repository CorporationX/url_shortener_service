package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "url")
public class UrlProperties {
    private String domain;
    private final Duration ttl = Duration.ofHours(24);

    public void setDomain(String domain) {
        this.domain = domain.endsWith("/") ? domain : domain + "/";
    }
}