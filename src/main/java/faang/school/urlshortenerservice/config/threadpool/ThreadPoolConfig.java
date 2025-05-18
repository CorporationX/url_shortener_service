package faang.school.urlshortenerservice.config.threadpool;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
@Validated
public class ThreadPoolConfig {

    @Value("${hash.thread.pool.hash-size:2}")
    @NotNull(message = "Hash pool size must not be null")
    @Min(value = 1, message = "Hash pool size must be at least 1")
    private Integer hashPoolSize;

    @Value("${hash.thread.pool.core-size:2}")
    @NotNull(message = "Core pool size must not be null")
    @Min(value = 1, message = "Core pool size must be at least 1")
    private Integer coreSize;

    @Value("${hash.thread.pool.max-size:4}")
    @NotNull(message = "Max pool size must not be null")
    @Min(value = 1, message = "Max pool size must be at least 1")
    private Integer maxSize;

    @Value("${hash.thread.pool.queue-capacity:100}")
    @NotNull(message = "Queue capacity must not be null")
    @Min(value = 1, message = "Queue capacity must be at least 1")
    private Integer queueCapacity;

    @Value("${hash.thread.pool.keep-alive-seconds:60}")
    @NotNull(message = "Keep alive seconds must not be null")
    @Min(value = 1, message = "Keep alive seconds must be at least 1")
    private Integer keepAliveSeconds;

    @Value("${hash.thread.pool.thread-name-prefix}")
    @NotBlank(message = "Thread name prefix must not be blank")
    private String threadNamePrefix;

    @PostConstruct
    public void  validate() {
        log.info("Initializing hashTaskExecutor with coreSize={}, maxSize={}, queueCapacity={}, prefix={}",
                coreSize, maxSize, queueCapacity, threadNamePrefix);

        if (coreSize > maxSize) {
            log.error("Core pool size cannot be greater than max pool size");
            throw new IllegalArgumentException("Core pool size cannot be greater than max pool size");
        }
    }

    @Bean(name = "hashTaskExecutor")
    public TaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("Initialized hashTaskExecutor with coreSize={}, maxSize={}, queueCapacity={}, prefix={}",
                coreSize, maxSize, queueCapacity, threadNamePrefix);

        return executor;
    }

    @Bean(name = "hashThreadPool")
    public ExecutorService hashThreadPool() {
        return Executors.newFixedThreadPool(hashPoolSize);
    }
}
