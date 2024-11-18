package faang.school.urlshortenerservice.config.thread;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
public class ExecutorsConfig {
    private final TaskExecutorProperties taskExecutorProperties;

    @Bean
    public ExecutorService fillUpCacheExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public TaskExecutor hashesGeneratorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(taskExecutorProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
