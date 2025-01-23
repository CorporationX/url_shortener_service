package faang.school.urlshortenerservice.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "cache")
public record CacheProperties(int maxCacheSize, long cacheUpdateThresholdPercentage, long cacheUpdateBatchPercentage) {
}
