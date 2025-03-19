package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.hash-cache")
@Data
public class HashCacheProperties {
    private int maxSize = 10000;
    private int refillThresholdPercent = 20;

    private int minDbMultiplier = 10;
    private int maxDbMultiplier = 20;

    private int batchSize = 1000;
    private int maxGenerationBatch = 100000;

    private int executorCorePoolSize = 2;
    private int executorMaxPoolSize = 4;
    private int executorQueueCapacity = 100;
}
