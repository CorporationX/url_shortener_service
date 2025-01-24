package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashCashExecutorConfig {
    @Value("${task.hash-cache-executor.pool.core-size:5}")
    private int poolSize;

    @Value("${task.hash-cache-executor.pool.max-size:10}")
    private int maxPoolSize;

    @Value("${task.hash-cache-executor.queue-capacity:100}")
    private int queueCapacity;

    @Bean
    public ExecutorService hashExecutorService() {
        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
