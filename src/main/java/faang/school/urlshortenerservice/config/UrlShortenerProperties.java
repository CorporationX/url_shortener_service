package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "url-shortener")
public record UrlShortenerProperties(
        String hostName,
        int localCacheCapacity,
        long hashAmountToLocalCache,  // Именно это имя метода будет сгенерировано
        double localCacheThresholdRatio,
        long hashAmountToGenerate,
        double hashDatabaseThresholdRatio
) {}