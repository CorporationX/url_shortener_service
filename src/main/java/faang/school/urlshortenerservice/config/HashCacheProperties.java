package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "hash.cache")
public class HashCacheProperties {
    private int capacity;
    private double refillThresholdPercentage;
}
