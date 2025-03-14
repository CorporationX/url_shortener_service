package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hash")
public class HashProperties {

    private int cacheSize;

    private int batchSize;

    private int cacheTtl;
}
