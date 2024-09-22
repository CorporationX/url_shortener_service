package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hash-cache")
public class HashCacheProperties {
    private int capacity;
    private int minimumRequiredSize;
}
