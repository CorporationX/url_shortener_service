package faang.school.urlshortenerservice.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "url-shortener")
public record UrlShortenerProperties(String hostName,
                                     int localCacheCapacity,
                                     int hashAmountToLocalCache,
                                     double localCacheThresholdRatio,
                                     int hashAmountToGenerate,
                                     double hashDatabaseThresholdRatio) {
}
