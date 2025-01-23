package faang.school.urlshortenerservice.config.hashcache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BlockingQueueCache {
    @Value("${queue-hash-cache.capacity}")
    private int capacity;

    @Value("${queue-hash-cache.pool-size}")
    private int getMoreHashesPoolSize;

    @Bean
    public BlockingQueue<String> hashCacheQueue() {
        return new ArrayBlockingQueue<>(capacity);
    }

    @Bean
    public ExecutorService getMoreHashesPool() {
        return Executors.newFixedThreadPool(getMoreHashesPoolSize);
    }
}
