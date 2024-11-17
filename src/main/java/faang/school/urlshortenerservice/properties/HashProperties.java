package faang.school.urlshortenerservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "hash-encoder")
public class HashProperties {
    private int generateSize;
    private int hashBatchSize;
    private int cacheCapacity;
    private double lowThresholdFactor;
}
