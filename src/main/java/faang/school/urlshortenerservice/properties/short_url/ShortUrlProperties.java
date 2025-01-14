package faang.school.urlshortenerservice.properties.short_url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "short-url")
@Component
public class ShortUrlProperties {

    private String baseDomain;
    private String basePath;

    private CacheSettings cacheSettings;
    private HashGenerationSettings hashGenerationSettings;

    @Getter
    @Setter
    public static class CacheSettings {
        private int popularTtlHours;
        private int defaultTtlMinutes;
        private String defaultCacheName;
        private int popularHashMaxCount;
        private String shortUrlRequestStatsCacheName;
        private String resetShortRequestUrlStatsCron;
    }

    @Getter
    @Setter
    public static class HashGenerationSettings {
        private int cachePreloadSize;
        private int dbCreateBatchSize;
    }
}
