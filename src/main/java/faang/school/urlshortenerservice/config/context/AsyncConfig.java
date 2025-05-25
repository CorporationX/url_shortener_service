package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${async.core-size}")
    private int coreSize;

    @Value("${async.max-size}")
    private int maxSize;

    @Value("${async.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorThreadPool")
    public Executor hashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("HashGenerator-");
        executor.setMaxPoolSize(maxSize);
        executor.setCorePoolSize(coreSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    @Bean(name = "cachePool")
    public Executor cacheThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("HashCash-");
        executor.setMaxPoolSize(maxSize);
        executor.setCorePoolSize(coreSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }
}
