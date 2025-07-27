package faang.school.urlshortenerservice.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "app.redis-cache.hash")
@Getter
@Setter
public class CacheProperties {
    private int ttl;
    private String keyPrefix;
}
