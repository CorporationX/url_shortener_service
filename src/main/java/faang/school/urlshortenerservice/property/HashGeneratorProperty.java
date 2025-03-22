package faang.school.urlshortenerservice.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "async.hash-generator")
@Component
public class HashGeneratorProperty {
    private String threadName;
    private int poolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int batchSize;
    private int minLimit;
}
