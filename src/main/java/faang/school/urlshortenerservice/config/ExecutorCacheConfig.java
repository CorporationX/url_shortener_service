package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

public class ExecutorCacheConfig {
    @Value("${hash.cache.executor.core-pool-size}")
    private int cacheCorePoolSize;
    @Value("${hash.cache.executor.max-pool-size}")
    private int cacheMaxPoolSize;
    @Value("${hash.cache.executor.queue-capacity}")
    private int CacheQueueCapacity;

    @Bean
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cacheCorePoolSize);
        executor.setMaxPoolSize(cacheMaxPoolSize);
        executor.setQueueCapacity(CacheQueueCapacity);
        executor.initialize();
        return executor;
    }
}
