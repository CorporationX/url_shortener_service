package faang.school.urlshortenerservice.config.redis.url_hash_cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.data.redis.urls-hash-cache.variables")
public class RedisUrlHashCacheProperties {
    String key;
    long capacity;
}
