package faang.school.urlshortenerservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "url-shortener.cache-properties")
public record CacheProperties(
        int corePoolSize,
        int maxPoolSize,
        int queueCapacity,
        String threadNamePrefix
) {}
