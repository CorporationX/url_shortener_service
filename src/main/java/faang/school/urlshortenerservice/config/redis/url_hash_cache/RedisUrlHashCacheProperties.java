package faang.school.urlshortenerservice.config.redis.url_hash_cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.redis.url-hash-cache")
public class RedisUrlHashCacheProperties {
    private String key;
    private long capacity;
    private int port;
    private String host;
    private int ttl;
}
