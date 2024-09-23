package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashCacheThreadPoolExecutor {

    @Value("${spring.task.execution.hash-cache-executor.core-size}")
    private int coreSize;

    @Value("${spring.task.execution.hash-cache-executor.max-size}")
    private int maxSize;

    @Value("${spring.task.execution.hash-cache-executor.queue-capacity}")
    private int queueCapacity;

    @Value("${spring.task.execution.hash-cache-executor.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "hashCacheTaskExecutor")
    public ThreadPoolTaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
