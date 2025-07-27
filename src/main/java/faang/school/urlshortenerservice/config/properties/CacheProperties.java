package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shortener.cache")
public record CacheProperties(
        @Positive
        int capacity,
        int batchSize,
        int threshold
) {
}
