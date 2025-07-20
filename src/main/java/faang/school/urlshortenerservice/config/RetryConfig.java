package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "retry.hash-generator")
public class RetryConfig {
    private int maxAttempts;
    private long delay;
    private double multiplier;
}
