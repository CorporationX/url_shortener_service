package faang.school.urlshortenerservice.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "retry")
@Data
public class RetryProperties {

    @Min(value = 1, message = "Max attempts must be at least 1")
    @Max(value = 10, message = "Max attempts must not exceed 10")
    private int maxAttempts = 3;

    @Min(value = 100, message = "Delay must be at least 100ms")
    @Max(value = 60000, message = "Delay must not exceed 60000ms (60 сек)")
    private long delayMs = 1000;
}

