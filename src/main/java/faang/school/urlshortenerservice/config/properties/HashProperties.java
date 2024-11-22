package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hash")
@Data
public class HashProperties {
    private int batchSize;
    private int maxSize;
    private int percentThreshold;
    private String interval;
}
