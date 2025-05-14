package faang.school.urlshortenerservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash-cache-setting")
public record HashCacheProperties(
        int queueSize,
        int percentageToGenerateNewHashes,
        int batchSize
) {
}
