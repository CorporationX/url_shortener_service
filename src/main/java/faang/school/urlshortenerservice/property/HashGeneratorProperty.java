package faang.school.urlshortenerservice.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "async.hash-generator")
public class HashGeneratorProperty {
    private String threadName;
    private int poolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int batchSize;
    private int minLimit;
}
