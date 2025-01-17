package faang.school.urlshortenerservice.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "cache")
public record CachePropertiesConfig(int size, double percentSizeToTriggerUpdate, double percentSizeToUpdate, long dbSizeToTriggerUpdate) {
}
