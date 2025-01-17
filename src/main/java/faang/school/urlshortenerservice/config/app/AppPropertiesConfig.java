package faang.school.urlshortenerservice.config.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application")
public record AppPropertiesConfig(String baseUrl) {
}
