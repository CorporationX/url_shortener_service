package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "hash-cache")
public class HashCacheProperties {
    private double minimumPercentageToAdd;
    private int size;
}
