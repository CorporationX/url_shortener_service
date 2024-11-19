package faang.school.urlshortenerservice.config.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "executor2.verify-hash-cache-executor")
public class HashCacheExecutorParams {
    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime;
}
