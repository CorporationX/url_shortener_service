package faang.school.urlshortenerservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {
    @Value("${spring.properties.thread-pool-size}")
    private int THREAD_POOL_SIZE;

    @Value("${spring.properties.cache-pros.initial-capacity}")
    private int initialCapacity;

    @Value("${spring.properties.cache-pros.maximum-capacity}")
    private int maximumCapacity;

    @Value("${spring.properties.cache-pros.expire}")
    private int expireAfterWrite;

    @Bean
    public ExecutorService customThreadPool() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumCapacity)
                .expireAfterWrite(expireAfterWrite, TimeUnit.DAYS));
        return cacheManager;
    }
}
