package faang.school.urlshortenerservice.config.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Configuration
public class CacheConfig {
    @Value("${shortener.hash.cache.capacity}")
    private int cacheCapacity;

    @Bean("hashCacheQueue")
    public BlockingQueue<String> hashCacheQueue() {
        return new ArrayBlockingQueue<>(cacheCapacity);
    }
}
