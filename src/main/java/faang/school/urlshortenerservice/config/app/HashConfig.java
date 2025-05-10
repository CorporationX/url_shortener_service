package faang.school.urlshortenerservice.config.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.hash")
@Getter
@Setter
public class HashConfig {
    private int batchSize;
}
