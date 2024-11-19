package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashThreadPoolConfig {
    @Value("${thread-pool.core_pool_size}")
    private int corePoolSize;

    @Value("${thread-pool.max_pool_size}")
    private int maxPoolSize;

    @Value("${thread-pool.queue_capacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashCacheExecutor-");
        executor.initialize();
        return executor;
    }
}
