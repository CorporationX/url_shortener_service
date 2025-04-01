package faang.school.urlshortenerservice.config.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "hash-generator.thread-pool")
public class HashGeneratorThreadPoolProperties {

    private int coreSize;
    private int maxSize;
    private int queueCapacity;
}
