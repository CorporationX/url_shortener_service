package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hash.generation")
@Data
public class HashGenerationProperties {
    private long generationRange;
    private String cron;
    private int localHashCapacity;
    private double minQueuePercent;
}
