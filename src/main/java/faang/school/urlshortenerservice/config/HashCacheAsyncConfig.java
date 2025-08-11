package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class HashCacheAsyncConfig {

    @Value("${url-shortener-service.hash-cache.thread-pool.core-size:5}")
    private int corePoolSize;

    @Value("${url-shortener-service.hash-cache.thread-pool.max-size:10}")
    private int maxPoolSize;

    @Value("${url-shortener-service.hash-cache.thread-pool.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = "hashCacheTaskExecutor")
    public Executor hashCacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-cache-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
