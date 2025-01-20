package faang.school.urlshortenerservice.properties.thread;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "thread.hash-cache")
public class HashCacheExecutorProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveSeconds;
    private int queueCapacity;
}