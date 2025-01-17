package faang.school.urlshortenerservice.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "url-shortener")
public record UrlShortenerProperties(String hostName,
                                     long localCacheCapacity,
                                     long hashAmountToLocalCache,
                                     double localCacheThresholdRatio,
                                     long hashAmountToGenerate,
                                     double hashDatabaseThresholdRatio) {
}
