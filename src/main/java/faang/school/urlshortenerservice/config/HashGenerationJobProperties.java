package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hash.generator.job")
public class HashGenerationJobProperties {
    private long delay;
    private int minHashes;
    private int batchSize;
}
