package faang.school.urlshortenerservice.config.app;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties("application")
public record AppPropertiesConfig(String baseUrl) {
}
