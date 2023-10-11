package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Value("${async.generator-thread-pool.settings.core-pool-size}")
    private int generatorCorePoolSize;
    @Value("${async.generator-thread-pool.settings.max-pool-size}")
    private int generatorMaxPoolSize;
    @Value("${async.generator-thread-pool.settings.queue-capacity}")
    private int generatorQueueCapacity;
    @Value("${async.cache-thread-pool.settings.core-pool-size}")
    private int cacheCorePoolSize;
    @Value("${async.cache-thread-pool.settings.max-pool-size}")
    private int cacheMaxPoolSize;
    @Value("${async.cache-thread-pool.settings.queue-capacity}")
    private int cacheQueueCapacity;

    @Bean("threadPoolForGenerateBatch")
    public Executor threadPoolForGenerateBatch() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(generatorCorePoolSize);
        executor.setMaxPoolSize(generatorMaxPoolSize);
        executor.setQueueCapacity(generatorQueueCapacity);
        executor.initialize();
        return executor;
    }

    @Bean("hashCacheThreadPool")
    public Executor hashCacheThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(cacheCorePoolSize);
        executor.setMaxPoolSize(cacheMaxPoolSize);
        executor.setQueueCapacity(cacheQueueCapacity);
        executor.initialize();
        return executor;
    }
}
