package faang.school.urlshortenerservice.config.hash;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hash.cache")
public record CacheProperties(
        int minGeneratedPercentage,
        int poolSize,
        int maxSize,
        int ttlDays
) {
}
