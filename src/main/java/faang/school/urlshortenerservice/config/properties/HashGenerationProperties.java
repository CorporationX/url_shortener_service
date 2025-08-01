package faang.school.urlshortenerservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hash-generation")
public class HashGenerationProperties {
    private int maximum;
    private String schedulerCron;
    private int queueCapacity;
    private double queueCriticalLoad;
    private int amountToPull;
    private String scheduledCleanupRate;
}
