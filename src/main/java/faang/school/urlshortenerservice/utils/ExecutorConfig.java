package faang.school.urlshortenerservice.utils;

import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService hashCacheExecutorService() {
        int corePoolSize = 4;
        int maxPoolSize = 10;
        int queueCapacity = 100;

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
    }
    @Bean(name = "hashGeneratorThreadPool")
    public Executor taskExecutor(TaskExecutionProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getPool().getCoreSize());
        executor.setMaxPoolSize(properties.getPool().getMaxSize());
        executor.setQueueCapacity(properties.getPool().getQueueCapacity());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}