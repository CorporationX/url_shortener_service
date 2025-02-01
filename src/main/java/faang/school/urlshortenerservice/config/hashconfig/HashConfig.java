package faang.school.urlshortenerservice.config.hashconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hash")
public class HashConfig {
    public HashConfig() {
        System.out.println("HashConfig created");
    }

    private int batchSize = 100;
    private ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();

    @Data
    public static class ThreadPoolConfig {
        private int coreSize = 5;
        private int maxSize = 10;
        private int queueSize = 50;
    }
}