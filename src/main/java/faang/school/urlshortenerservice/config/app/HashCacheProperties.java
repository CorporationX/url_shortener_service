package faang.school.urlshortenerservice.config.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "app.hash-cache")
public class HashCacheProperties {
    private int maxSize;
    private int refillThreshold;
    private int initialDbSize;
    private int cacheTtl;

    @NestedConfigurationProperty
    private ExecutorConfig executorConfig = new ExecutorConfig();

    @Data
    public static class ExecutorConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private int keepAliveTime;
        private int queueCapacity;
    }
}