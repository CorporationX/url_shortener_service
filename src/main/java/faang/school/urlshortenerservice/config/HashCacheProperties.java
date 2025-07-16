package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "hash-cache")
public class HashCacheProperties {
    @Value("${hash-cache.max-size}")
    private int maxSize;
    @Value("${hash-cache.refill-threshold}")
    private double refillThreshold;
}
