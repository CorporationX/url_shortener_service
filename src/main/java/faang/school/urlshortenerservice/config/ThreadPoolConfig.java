package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {
    @Value("${hash-generator.thread-pool.size:5}")
    private int hashGeneratorPoolSize;

    @Value("${hash-generator.thread-pool.queue-capacity:50}")
    private int hashGeneratorQueueCapacity;
    @Value("${hash-cache.thread-pool.size:5}")
    private int hashCachePoolSize;

    @Value("${hash-cache.thread-pool.queue-capacity:50}")
    private int hashCacheQueueCapacity;

    @Bean
    public Executor hashGeneratorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorPoolSize);
        executor.setMaxPoolSize(hashGeneratorPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor hashCacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCachePoolSize);
        executor.setMaxPoolSize(hashCachePoolSize);
        executor.setQueueCapacity(hashCacheQueueCapacity);
        executor.initialize();
        return executor;
    }
}
