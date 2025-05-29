package faang.school.urlshortenerservice.config.hash;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Getter
@Validated
public class HashCacheConfig {
    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.refill-threshold-percent}")
    private int refillThresholdPercent;

    @Value("${hash.cache.executor.core-pool-size}")
    private int executorCorePoolSize;

    @Value("${hash.cache.executor.max-pool-size}")
    private int executorMaxPoolSize;

    @Value("${hash.cache.executor.queue-capacity}")
    private int executorQueueCapacity;

    @Bean
    public BlockingQueue<String> availableHashesQueue() {
        return new ArrayBlockingQueue<>(maxSize);
    }

    @Bean
    public ExecutorService hashRefillExecutor() {
        return new ThreadPoolExecutor(
                executorCorePoolSize,
                executorMaxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(executorQueueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}