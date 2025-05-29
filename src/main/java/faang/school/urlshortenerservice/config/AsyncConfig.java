package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Value("${async.pool.hash-generator.core-pool-size}")
    private int hashGeneratorPoolSize;

    @Value("${async.pool.hash-generator.queue-capacity}")
    private int hashGeneratorQueueCapacity;

    @Value("${async.executor.hash-cache.core-pool-size}")
    private int corePoolSize;

    @Value("${async.executor.hash-cache.max-pool-size}")
    private int maxPoolSize;

    @Value("${async.executor.hash-cache.keep-alive}")
    private int keepAlive;

    @Value("${async.executor.hash-cache.queue-capacity}")
    private int queueCapacity;

    @Bean
    public Executor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.setThreadNamePrefix("hash-generator-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService hashCacheExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAlive,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
