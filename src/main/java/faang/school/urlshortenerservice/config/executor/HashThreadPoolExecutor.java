package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashThreadPoolExecutor {

    @Value("${spring.task.execution.hash-executor.core-size}")
    private int coreSize;

    @Value("${spring.task.execution.hash-executor.max-size}")
    private int maxSize;

    @Value("${spring.task.execution.hash-executor.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashTaskExecutor")
    public ThreadPoolTaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hashAsyncThread-");
        executor.initialize();
        return executor;
    }
}
