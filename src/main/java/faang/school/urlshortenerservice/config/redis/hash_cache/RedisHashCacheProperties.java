package faang.school.urlshortenerservice.config.redis.hash_cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.data.redis.hash-cache.variables")
public class RedisHashCacheProperties {
    private String key;
    private int hashLength;
    private long batchSize;
    private int capacity;
}