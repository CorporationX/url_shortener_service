package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shortener.cache")
public record CacheProperties(
        int capacity,
        int batchSize,
        int threshold
) {
}
