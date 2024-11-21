package faang.school.urlshortenerservice.config.threadPool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="thread.pool")
public class ThreadPoolProperties {

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int queueCapacity;
}
