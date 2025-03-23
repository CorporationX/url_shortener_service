package faang.school.urlshortenerservice.config.executor;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "thread-pool.generate-hash")
public class GenerateHashExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;
}