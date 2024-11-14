package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadProperties {
    private Generator generator = new Generator();
    private Cache cache = new Cache();

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
        private double percentage;
    }
}
