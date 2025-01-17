package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache")
public record CachePropertiesConfig(int size, double percentSizeToTriggerUpdate, double percentSizeToUpdate, long dbSizeToTriggerUpdate) {
}
