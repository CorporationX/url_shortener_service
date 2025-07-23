package faang.school.urlshortenerservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Getter
public class HashCacheConfig {

    @Value("${hash.capacity:1000}")
    private int capacity;

    @Value("${hash.threshold.percent:0.2}")
    private double thresholdPercent;
}
