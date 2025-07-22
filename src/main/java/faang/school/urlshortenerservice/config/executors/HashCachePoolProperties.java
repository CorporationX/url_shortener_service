package faang.school.urlshortenerservice.config.executors;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("executors.hash-cache")
public record HashCachePoolProperties(
        int poolSize,
        int queueCapacity,
        int awaitSeconds
) {
}
