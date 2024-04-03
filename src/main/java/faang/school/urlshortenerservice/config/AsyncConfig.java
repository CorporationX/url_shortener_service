package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Value("${async.generator_thread_pool.core_pool_size}")
    private int generatorCorePoolSize;
    @Value("${async.generator_thread_pool.max_pool_size}")
    private int generatorMaxPoolSize;
    @Value("${async.generator_thread_pool.queue_capacity}")
    private int generatorQueueCapacity;
    @Value("${async.cache_thread_pool.core_pool_size}")
    private int hashCacheCorePoolSize;
    @Value("${async.cache_thread_pool.max_pool_size}")
    private int hashCacheMaxPoolSize;
    @Value("${async.cache_thread_pool.queue_capacity}")
    private int hashCacheQueueCapacity;
    @Value("${async.scheduler_thread_pool.core_pool_size}")
    private int schedulerCorePoolSize;
    @Value("${async.scheduler_thread_pool.max_pool_size}")
    private int schedulerMaxPoolSize;
    @Value("${async.scheduler_thread_pool.queue_capacity}")
    private int schedulerQueueCapacity;

    @Bean(name = "generateBatchThreadPool")
    public TaskExecutor generateBatchThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(generatorCorePoolSize);
        executor.setMaxPoolSize(generatorMaxPoolSize);
        executor.setQueueCapacity(generatorQueueCapacity);

        executor.initialize();
        return executor;
    }

    @Bean(name = "hashCacheThreadPool")
    public TaskExecutor hashCacheThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(hashCacheCorePoolSize);
        executor.setMaxPoolSize(hashCacheMaxPoolSize);
        executor.setQueueCapacity(hashCacheQueueCapacity);

        executor.initialize();
        return executor;
    }

    @Bean(name = "schedulerThreadPool")
    public TaskExecutor schedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(schedulerCorePoolSize);
        executor.setMaxPoolSize(schedulerMaxPoolSize);
        executor.setQueueCapacity(schedulerQueueCapacity);

        executor.initialize();
        return executor;
    }
}