package faang.school.urlshortenerservice.config.hash;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Min(value = 1, message = "Max size must be at least 1")
    private int maxSize;

    @Value("${hash.cache.refill-threshold-percent}")
    @Min(value = 1, message = "Refill threshold must be at least 1%")
    @Max(value = 100, message = "Refill threshold must not exceed 100%")
    private int refillThresholdPercent;

    @Value("${hash.cache.executor.core-pool-size}")
    @Min(value = 1, message = "Core pool size must be at least 1")
    private int executorCorePoolSize;

    @Value("${hash.cache.executor.max-pool-size}")
    @Min(value = 1, message = "Max pool size must be at least 1")
    private int executorMaxPoolSize;

    @Value("${hash.cache.executor.queue-capacity}")
    @Min(value = 1, message = "Queue capacity must be at least 1")
    private int executorQueueCapacity;

    @Bean("availableHashesQueue")
    public BlockingQueue<String> freeHashesQueue() {
        return new ArrayBlockingQueue<>(maxSize);
    }

    @Bean("hashRefillExecutor")
    public ExecutorService hashRefillExecutor() {
        return new ThreadPoolExecutor(
                executorCorePoolSize,
                executorMaxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(executorQueueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}