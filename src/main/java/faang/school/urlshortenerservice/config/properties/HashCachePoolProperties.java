package faang.school.urlshortenerservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("executors.hash-cache")
public record HashCachePoolProperties(
        int poolSize,
        int queueCapacity,
        int awaitSeconds,
        String threadNamePrefix
) {
}
