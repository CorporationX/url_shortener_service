package faang.school.urlshortenerservice.properties.short_url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "short-url.hash")
@Component
public class HashProperties {

    private int cacheCapacity;
    private int dbCreateMaxCount;
    private int dbCreateBatchSize;
    private int minPercentageThreshold;
}
