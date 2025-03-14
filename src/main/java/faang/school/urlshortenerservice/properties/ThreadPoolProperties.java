package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {

    private int cacheSize;

    private int corePoolSize;

    private int maxPoolSize;

    private int keepAliveTime;

    private int queueCapacity;

    private double refillThreshold;

    private int batchSize;
}
