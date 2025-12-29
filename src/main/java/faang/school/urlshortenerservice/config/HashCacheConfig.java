package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class HashCacheConfig {

    @Bean(name = "hashCacheExecutor")
    public ExecutorService hashCacheExecutor(
            @Value("${hash.cache.executor.core-pool-size:2}") int corePoolSize,
            @Value("${hash.cache.executor.max-pool-size:4}") int maxPoolSize,
            @Value("${hash.cache.executor.queue-capacity:50}") int queueCapacity,
            @Value("${hash.cache.executor.keep-alive-seconds:60}") long keepAliveSeconds) {
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                r -> {
                    Thread thread = new Thread(r, "hash-cache-executor-");
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        log.info("HashCache ExecutorService configured: corePoolSize={}, maxPoolSize={}, queueCapacity={}, keepAliveSeconds={}", 
                corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds);
        
        return executor;
    }
}

