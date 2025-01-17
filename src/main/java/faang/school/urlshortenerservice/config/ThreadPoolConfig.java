package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ThreadPoolConfig {
    @Value("${thread.pool.core-size}")
    private int corePoolSize;
    @Value("${thread.pool.max-size}")
    private int maxPoolSize;
    @Value("${thread.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${thread.pool.task-prefix}")
    private String taskPrefix;
    @Value("${thread.pool.fixed-size}")
    private int fixedPoolSize;

    @Bean(destroyMethod = "shutdown")
    public TaskExecutor queueTaskThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(taskPrefix);
        executor.initialize();
        log.info("Thread pool created with corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                corePoolSize, maxPoolSize, queueCapacity);
        ;

        return executor;
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedPoolSize);
    }
}
