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
} 