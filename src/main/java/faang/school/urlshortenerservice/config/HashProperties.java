package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hash")
public class HashProperties {
    private int batchSize;
    private int threadPoolSize;
    private int threadQueueSize;
    private int fillPercent;
}