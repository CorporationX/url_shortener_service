package faang.school.urlshortenerservice.cache;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.hash-cache")
public class HashCacheProperty {
    private int maxQueueSize;
    private int refillThresholdPercent;
    private int amountHash;
}
