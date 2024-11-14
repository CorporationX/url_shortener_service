package faang.school.urlshortenerservice.config.hash;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "hash")
@ConfigurationPropertiesScan
@Data
public class HashProperties {
    private int batchSize;
    private int countToReturning;
}
