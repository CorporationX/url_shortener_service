package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadProperties {
    private Generator generator;
    private Cache cache;

    @Data
    public static class Generator {
        private int core;
        private int max;
        private int queue;
    }

    @Data
    public static class Cache {
        private int core;
        private int max;
        private int queue;
    }
}
