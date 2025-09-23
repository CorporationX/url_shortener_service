package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * AsyncConfig — конфигурация для async
 *
 * @author bozya
 * @since 16.09.2025
 */
@EnableAsync
@Configuration
public class AsyncConfig {
    @Value("${async.thread-pool.size}")
    private int poolSize;

    @Value("${async.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean("customTaskExecutor")
    public TaskExecutor customTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize * 2);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}