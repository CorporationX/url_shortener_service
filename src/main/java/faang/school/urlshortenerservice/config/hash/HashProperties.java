package faang.school.urlshortenerservice.config.hash;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hash")
public record HashProperties(
        int maxBatchSize,
        int maxLength,
        int generatedCount,
        int poolSize,
        int queueSize,
        int daysBeforeClean
) {
}
