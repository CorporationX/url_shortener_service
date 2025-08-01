package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Value("${url-shortener.thread-pool.hash-generator.core-size}")
    private int hashGeneratorCorePoolSize;

    @Value("${url-shortener.thread-pool.hash-generator.max-size}")
    private int hashGeneratorMaxPoolSize;

    @Value("${url-shortener.thread-pool.hash-generator.queue-capacity}")
    private int hashGeneratorQueueCapacity;

    @Value("${url-shortener.thread-pool.hash-cache.core-size}")
    private int hashCacheCorePoolSize;

    @Bean(name = "hashGeneratorTaskExecutor")
    public Executor hashGeneratorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorCorePoolSize);
        executor.setMaxPoolSize(hashGeneratorMaxPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.setThreadNamePrefix("hash-generator-");
        executor.initialize();

        log.info("Initialized hash generator thread pool with core size: {}, max size: {}, queue capacity: {}", 
                hashGeneratorCorePoolSize, hashGeneratorMaxPoolSize, hashGeneratorQueueCapacity);

        return executor;
    }

    @Bean(name = "hashCacheExecutorService")
    public ExecutorService hashCacheExecutorService() {
        ExecutorService executorService = Executors.newFixedThreadPool(hashCacheCorePoolSize);
        log.info("Initialized hash cache executor service with thread pool size: {}", hashCacheCorePoolSize);
        return executorService;
    }
}
