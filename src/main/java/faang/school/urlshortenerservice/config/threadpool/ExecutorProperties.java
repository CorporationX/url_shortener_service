package faang.school.urlshortenerservice.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "executor")
public class ExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
