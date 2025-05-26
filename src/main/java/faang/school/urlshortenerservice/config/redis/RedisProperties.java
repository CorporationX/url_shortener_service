package faang.school.urlshortenerservice.config.redis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    
    @NotBlank(message = "Redis host cannot be blank")
    private String host;
    
    @Positive(message = "Redis port must be positive")
    private int port;
    
    @Positive(message = "Connection timeout must be positive")
    private int connectionTimeout;
    
    @Positive(message = "Read timeout must be positive")
    private int readTimeout;
    
    private String password;
    
    @Positive(message = "Maximum active connections must be positive")
    private int maxActive;
    
    @Positive(message = "Maximum idle connections must be positive")
    private int maxIdle;
    
    @Positive(message = "Minimum idle connections must be positive")
    private int minIdle;
} 