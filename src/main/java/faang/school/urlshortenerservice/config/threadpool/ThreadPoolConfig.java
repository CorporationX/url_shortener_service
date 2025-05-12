package faang.school.urlshortenerservice.config.threadpool;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    @Value("${hash.thread.fixed.pool-size}")
    @NotNull(message = "Hash pool size must be specified")
    @Min(value = 1, message = "Hash pool size must be positive")
    private Integer hashPoolSize;

    @Value("${hash.thread.task.pool-size}")
    @NotNull(message = "Core pool size must be specified")
    @Min(value = 1, message = "Core pool size must be positive")
    private Integer corePoolSize;

    @Value("${hash.thread.task.max-pool-size}")
    private Integer maxPoolSize;

    @Value("${hash.thread.task.capacity}")
    @NotNull(message = "Queue capacity must be specified")
    @Min(value = 1, message = "Queue capacity must be positive")
    private Integer queueCapacity;

    @Value("${hash.thread.task.prefix}")
    @NotNull(message = "Task prefix must be not null")
    @NotBlank(message = "Task prefix must be not blank")
    private String taskPrefix;

    @PostConstruct
    private void init() {
        if (maxPoolSize != null && maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException("Max pool size must be greater than or equal to core pool size");
        }
        log.info("ThreadPoolConfig initialized with hashPoolSize={}, corePoolSize={}, maxPoolSize={}, " +
                        "queueCapacity={}, taskPrefix={}",
                hashPoolSize, corePoolSize, maxPoolSize, queueCapacity, taskPrefix);
    }

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
