package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "url")
public class UrlProperties {
    private Duration retentionPeriod;
}
