package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {

    @Bean("hashCacheExecutor")
    public ThreadPoolTaskExecutor hashCacheExecutor(
            @Value("${app.hash.cache.executor.core-pool-size}") int corePoolSize,
            @Value("${app.hash.cache.executor.max-pool-size}") int maxPoolSize,
            @Value("${app.hash.cache.executor.queue-capacity}") int queueCapacity,
            @Value("${app.hash.cache.executor.thread-name-prefix:hash-cache-}") String threadNamePrefix
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-cache-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @Bean("hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor(
            @Value("${app.hash.generator.executor.core-pool-size}") int corePoolSize,
            @Value("${app.hash.generator.executor.max-pool-size}") int maxPoolSize,
            @Value("${app.hash.generator.executor.queue-capacity}") int queueCapacity,
            @Value("${app.hash.generator.executor.thread-name-prefix:hash-generator-}") String threadNamePrefix
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-generator-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
