package faang.school.urlshortenerservice.config.properties.hash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hash")
public class HashProperties {

    private Batch batch;
    private ThreadPool threadPool;
    private Queue queue;
    private Cache cache;

    @Getter
    @Setter
    public static class Batch {

        private int save;
        private int get;
    }

    @Getter
    @Setter
    public static class ThreadPool {

        private int initialPoolSize;
        private int maxPoolSize;
    }

    @Getter
    @Setter
    public static class Queue {

        private int capacity;
    }

    @Getter
    @Setter
    public static class Cache {

        private int capacity;
        private double fillPercent;
    }
}
