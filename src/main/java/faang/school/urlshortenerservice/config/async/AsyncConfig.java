package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean
    public Executor hashGeneratorExecutorPool(@Value("${app.async.hash_generator.core_pool_size}") int corePoolSize,
                                              @Value("${app.async.hash_generator.max_pool_size}") int maxPoolSeize,
                                              @Value("${app.async.hash_generator.queue_capacity}") int queueCapacity,
                                              @Value("${app.async.hash_generator.thread_name_prefix}") String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSeize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();

        return executor;
    }

    @Bean
    public Executor hashCacheExecutorPool(@Value("${app.async.hash_cache.core_pool_size}") int corePoolSize,
                                          @Value("${app.async.hash_cache.max_pool_size}") int maxPoolSeize,
                                          @Value("${app.async.hash_cache.queue_capacity}") int queueCapacity,
                                          @Value("${app.async.hash_cache.thread_name_prefix}") String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSeize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();

        return executor;
    }
}
