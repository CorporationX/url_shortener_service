package faang.school.urlshortenerservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class TaskExecutorConfig {

    private final TaskExecutorParams taskExecutorParams;
    private final TaskExecutorServiceParams taskExecutorServiceParams;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorParams.getCorePoolSize());
        executor.setMaxPoolSize(taskExecutorParams.getMaxPoolSize());
        executor.setQueueCapacity(taskExecutorParams.getQueueCapacity());
        executor.setThreadNamePrefix(taskExecutorParams.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService executorService() {
        ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
        executorService.setCorePoolSize(taskExecutorServiceParams.getCorePoolSize());
        executorService.setMaxPoolSize(taskExecutorServiceParams.getMaxPoolSize());
        executorService.setQueueCapacity(taskExecutorServiceParams.getQueueCapacity());
        executorService.setThreadNamePrefix(taskExecutorServiceParams.getThreadNamePrefix());
        executorService.initialize();
        return executorService.getThreadPoolExecutor();
    }
}
