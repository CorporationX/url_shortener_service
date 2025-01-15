package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Value("${async.executor.core_pool_size}")
    private int corePoolSize;

    @Value("${async.executor.max_pool_size}")
    private int maxPoolSize;

    @Value("${async.executor.queue_capacity}")
    private int queueCapacity;

    @Value("${async.executor.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean(name = "hashTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
