package faang.school.urlshortenerservice.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "url-shortener")
public record UrlShortenerProperties(String hostName,
                                     int localCacheCapacity,
                                     long hashAmountToLocalCache,
                                     double localCacheThresholdRatio,
                                     long hashAmountToGenerate,
                                     double hashDatabaseThresholdRatio) {
}

