package faang.school.urlshortenerservice.config.property;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.shortener")
@Validated
public class ShortenerProperties {
    @Min(1)
    private Integer maxAttempts;
}