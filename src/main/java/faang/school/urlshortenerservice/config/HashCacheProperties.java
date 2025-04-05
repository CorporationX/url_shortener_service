package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.hash-cache")
@Data
public class HashCacheProperties {
    private int maxSize;
    private int refillThresholdPercent;
    private int minDbMultiplier;
    private int maxDbMultiplier;
}
