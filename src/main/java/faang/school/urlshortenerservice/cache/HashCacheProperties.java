package faang.school.urlshortenerservice.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cache.hash")
@Getter
@Setter
public class HashCacheProperties {
    private String keyPrefix;
    private long ttl;
}
