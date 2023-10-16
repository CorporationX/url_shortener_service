package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorServiceConfig {

    @Value("${executor_thread_pool.queue_capacity}")
    private int queueCapacity;

    @Value("${executor_thread_pool.max_pool_size}")
    private int maxPoolSize;

    @Value("${executor_thread_pool.core_pool_size}")
    private int corePoolSize;

    @Value("${executor_thread_pool.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(queueCapacity);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
