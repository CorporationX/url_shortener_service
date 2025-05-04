package faang.school.urlshortenerservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "url")
public class UrlProperties {
    private String domain;

    public void setDomain(String domain) {
        this.domain = domain.endsWith("/") ? domain : domain + "/";
    }
}