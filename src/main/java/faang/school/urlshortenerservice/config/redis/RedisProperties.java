package faang.school.urlshortenerservice.config.redis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties("spring.redis")
@Configuration
public class RedisProperties {

    @NotBlank(message = "Redis host must be present")
    private String host;

    @Positive(message = "Redis port cannot be negative")
    private int port;

    @NotBlank(message = "Redis password must be present")
    private String password;
}