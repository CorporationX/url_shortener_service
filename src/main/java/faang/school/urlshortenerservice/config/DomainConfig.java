package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.domain")
public class DomainConfig {
    private String port;
    private String protocol;
    private String host;

    public String getBaseUrl() {
        return String.format("%s//%s:%s", protocol, host, port);
    }
}
