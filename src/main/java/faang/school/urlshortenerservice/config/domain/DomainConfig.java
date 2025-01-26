package faang.school.urlshortenerservice.config.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.domain")
@Data
public class DomainConfig {

    private String host;
    private String port;
    private String protocol;

    public String getBaseUrl() {
        return String.format("%s://%s:%s", protocol, host, port);
    }
}
