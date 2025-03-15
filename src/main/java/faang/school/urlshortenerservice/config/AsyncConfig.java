package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${hash.generator.thread-pool-size:5}")
    private int threadPoolSize;

    @Value("${hash.generator.queue-capacity:50}")
    private int queueCapacity;

    @Value("${hash.cache.thread-pool-size:5}")
    private int cacheThreadPoolSize;

    @Value("${hash.cache.queue-capacity:50}")
    private int cacheQueueCapacity;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setMaxPoolSize(threadPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerator-Thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cacheThreadPoolSize);
        executor.setMaxPoolSize(cacheThreadPoolSize);
        executor.setQueueCapacity(cacheQueueCapacity);
        executor.setThreadNamePrefix("HashCache-Thread-");
        executor.initialize();
        return executor;
    }
}
