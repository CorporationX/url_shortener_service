package faang.school.urlshortenerservice.util.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.hash.cache")
public class HashCacheProperty {

    private int maxQueueSize;
    private int refillPercent;
    private int amountHash;
}