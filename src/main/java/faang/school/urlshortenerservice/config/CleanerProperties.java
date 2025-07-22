package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shortener.cleaner")
public record CleanerProperties(
        int expiryDate,
        int batchSize,
        int maxBatches
) {
}
