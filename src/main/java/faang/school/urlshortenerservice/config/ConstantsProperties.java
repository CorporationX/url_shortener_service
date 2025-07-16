package faang.school.urlshortenerservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.constants")
public class ConstantsProperties {
    private int generationBathSize;
    private int localCachingSize;
    private int generationThresholdPercent;

    private int cacheGenThreshold;

    @PostConstruct
    private void calculateCacheGenThreshold() {
        cacheGenThreshold = localCachingSize * generationThresholdPercent / 100;
    }
}
