package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class CacheConfig {

    @Value("${spring.cache.redis.size}")
    private int cacheSize;

    @Bean
    public ArrayBlockingQueue<String> cache() {
        return new ArrayBlockingQueue<>(cacheSize * 2);
    }
}
