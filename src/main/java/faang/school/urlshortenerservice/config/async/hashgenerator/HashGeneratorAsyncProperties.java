package faang.school.urlshortenerservice.config.async.hashgenerator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "hash-generator.async")
public class HashGeneratorAsyncProperties {
    
    @Positive(message = "Core pool size must be positive")
    private int corePoolSize;
    
    @Positive(message = "Max pool size must be positive")
    private int maxPoolSize;
    
    @Min(value = 1, message = "Queue capacity must be at least 1")
    private int queueCapacity;
    
    @NotBlank(message = "Thread name prefix must not be blank")
    private String threadNamePrefix;
    
    @Positive(message = "Shutdown timeout must be positive")
    private int shutdownTimeoutSeconds;
    
    public void validatePoolSizes() {
        if (maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException(
                    String.format("Max pool size (%d) must be greater than or equal to core pool size (%d)", 
                            maxPoolSize, corePoolSize));
        }
    }
}