package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hash-thread-pool")
@Data
public class HashThreadPoolConfig {
    private int queueCapacity;
    private int maxPoolSize;
    private int corePoolSize;
    private String threadNamePrefix;
}

