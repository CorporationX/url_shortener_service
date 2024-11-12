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

    private BatchValues batchValues;
    private ThreadPool threadPool;
    private Queue queue;

    @Getter
    @Setter
    public static class BatchValues {

        private int saveBatch;
        private int getBatch;
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
}
