package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {
    @Value("${hash.thread.fixed.pool-size}")
    private int hashPoolSize;

    @Value("${hash.thread.task.pool-size}")
    private int corePoolSize;

    @Value("${hash.thread.task.max-pool-size}")
    private int maxPoolSize;

    @Value("${hash.thread.task.capacity}")
    private int queueCapacity;

    @Value("${hash.thread.task.prefix}")
    private String taskPrefix;

    @Bean
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(hashPoolSize);
    }

    @Bean
    public TaskExecutor queueTaskThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(taskPrefix);
        executor.initialize();
        return executor;
    }
}
