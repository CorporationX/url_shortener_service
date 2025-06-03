package faang.school.urlshortenerservice.config.threadpool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@Validated
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadPoolProperties threadPoolProperties;

    @Bean(name = "hashTaskExecutor")
    public TaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCoreSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("Initialized hashTaskExecutor with coreSize={}, maxSize={}, queueCapacity={}, prefix={}",
                threadPoolProperties.getCoreSize(), threadPoolProperties.getMaxSize(),
                threadPoolProperties.getQueueCapacity(), threadPoolProperties.getThreadNamePrefix());

        return executor;
    }

    @Bean(name = "hashThreadPool")
    public ExecutorService hashThreadPool() {
        return Executors.newFixedThreadPool(threadPoolProperties.getHashPoolSize());
    }
}
