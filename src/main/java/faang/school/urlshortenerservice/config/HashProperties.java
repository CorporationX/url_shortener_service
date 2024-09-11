package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hash")
@Data
public class HashProperties {
    private Integer batchSize;
    private Integer hashSize;
    private Integer uniqueNumbersSize;
}
