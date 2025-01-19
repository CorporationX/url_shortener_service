package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hash-generator")
public class HashGeneratorProperties {

    private int batchSize;

    private ThreadPool threadPool = new ThreadPool();

    @Data
    public static class ThreadPool {
        private int coreSize;
        private int maxSize;
        private int queueCapacity;
    }
}

