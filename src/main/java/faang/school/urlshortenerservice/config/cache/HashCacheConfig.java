package faang.school.urlshortenerservice.config.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class HashCacheConfig {

    @Value("${hash.cache.cache_pool_size}")
    private int cachePoolSize;

    @Bean(name = "hashCachePool")
    public ExecutorService hashCachePool() {
        return Executors.newFixedThreadPool(cachePoolSize);
    }
}