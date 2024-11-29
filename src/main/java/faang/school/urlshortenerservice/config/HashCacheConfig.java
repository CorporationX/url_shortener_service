package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashCacheConfig {

    @Value("${app.hash.cache.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${app.hash.cache.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.hash.cache.executor.queue-capacity}")
    private int queueCapacity;

    @Bean("hashCacheExecutor")
    public ExecutorService hashCacheExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity)
        );
    }
}
