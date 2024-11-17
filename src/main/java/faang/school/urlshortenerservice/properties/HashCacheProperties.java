package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hash-cache")
public class HashCacheProperties {
    private double minimumPercentageToAdd;
    private int size;
}
