package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "database")
public record DatabaseProperties(long dbSizeToTriggerUpdate) {
}
