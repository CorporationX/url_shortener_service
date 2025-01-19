package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hash-cache")
public class HashCacheProperties {

    private int maxSize;

    private int refillThresholdPercent;

    private ThreadPoolProperties threadPool = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {
        private int coreSize;
        private int maxSize;
        private int queueCapacity;
    }
}
