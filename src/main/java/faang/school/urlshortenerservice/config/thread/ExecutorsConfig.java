package faang.school.urlshortenerservice.config.thread;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
public class ExecutorsConfig {
    @Value("${thread-pool.fill-up-cache.size:1}")
    private int threadPoolSize;

    private final TaskExecutorProperties taskExecutorProperties;

    @Bean
    public ExecutorService fillUpCacheExecutorService() {
        return new ThreadPoolExecutor(
                threadPoolSize,
                threadPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(threadPoolSize));
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
