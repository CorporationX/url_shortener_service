package faang.school.urlshortenerservice.config.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CacheConfig {
    @Value("${hash.cache.pool_size}")
    private int cachePoolSize;

    @Bean
    public ExecutorService hashCachePool() {
        return Executors.newFixedThreadPool(cachePoolSize);
    }
}
