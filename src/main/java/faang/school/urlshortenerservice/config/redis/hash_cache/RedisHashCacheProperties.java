package faang.school.urlshortenerservice.config.redis.hash_cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.redis.hash-cache")
public class RedisHashCacheProperties {
    private String key;
    private int hashLength;
    private long batchSize;
    private int capacity;
    private int port;
    private String host;
}