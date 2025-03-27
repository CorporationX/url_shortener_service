package faang.school.urlshortenerservice.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hash.cache")
public class HashCacheConfig {
    private int maxSize;
    private int thresholdPercentage;
}