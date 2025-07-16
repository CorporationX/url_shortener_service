package faang.school.urlshortenerservice.config.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hash-generator")
public class HashGeneratorConfig {
    private int batchSize;
}