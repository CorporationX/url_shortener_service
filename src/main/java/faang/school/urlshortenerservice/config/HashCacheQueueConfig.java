package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class HashCacheQueueConfig {

    @Value("${params.cache-queue.capacity}")
    private int capacity;

    @Bean
    public ArrayBlockingQueue<String> hashCacheQueue() {
        return new ArrayBlockingQueue<>(capacity);
    }
}
