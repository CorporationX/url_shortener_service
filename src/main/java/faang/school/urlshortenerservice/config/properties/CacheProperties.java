package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server.cache")
public class CacheProperties {
    private long requestThreshold;
    private long ttlIncrementTimeMs;
    private long ttlInitialTimeSec;
}