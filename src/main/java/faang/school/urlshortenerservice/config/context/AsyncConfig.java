package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${async.hash.maxPoolSize}")
    private int hashMaxPoolSize;
    @Value("${async.hash.corePoolSize}")
    private int hashCorePoolSize;
    @Value("${async.hash.queueCapacity}")
    private int hashQueueCapacity;
    @Value("${async.cache.maxPoolSize}")
    private int cacheMaxPoolSize;
    @Value("${async.cache.corePoolSize}")
    private int cacheCorePoolSize;
    @Value("${async.cache.queueCapacity}")
    private int cacheQueueCapacity;

    public Executor getCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(cacheCorePoolSize);
        executor.setCorePoolSize(cacheMaxPoolSize);
        executor.setQueueCapacity(cacheQueueCapacity);
        executor.initialize();

        return executor;
    }

    public Executor getBatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(hashCorePoolSize);
        executor.setCorePoolSize(hashMaxPoolSize);
        executor.setQueueCapacity(hashQueueCapacity);
        executor.initialize();

        return executor;
    }

    @Bean
    public Executor cachedThreadPool() {
        return getCacheExecutor();
    }

    @Bean
    public Executor batchThreadPool() {
        return getBatchExecutor();
    }
}