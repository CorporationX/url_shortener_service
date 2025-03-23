package faang.school.urlshortenerservice.config.executor;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "thread-pool.receive-hash")
public class ReceiveHashExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;
}