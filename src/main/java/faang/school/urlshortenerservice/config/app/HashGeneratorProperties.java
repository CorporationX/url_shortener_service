package faang.school.urlshortenerservice.config.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.hash-generator")
@Getter
@Setter
public class HashGeneratorProperties {
    private int batchSize;
    private int threadPoolSize;
    private int threadPoolQueueSize;
}
