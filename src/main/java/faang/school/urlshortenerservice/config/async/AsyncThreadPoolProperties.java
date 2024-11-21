package faang.school.urlshortenerservice.config.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server.async.thread.pool")
public class AsyncThreadPoolProperties {

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int queueCapacity;
}
