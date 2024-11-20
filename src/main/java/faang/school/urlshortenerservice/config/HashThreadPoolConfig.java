package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashThreadPoolConfig {
    @Value("${thread-pool.core_pool_size}")
    private int corePoolSize;

    @Value("${thread-pool.queue_capacity}")
    private int queueCapacity;

    @Value("${thread-pool.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean
    public ThreadPoolTaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }
}
