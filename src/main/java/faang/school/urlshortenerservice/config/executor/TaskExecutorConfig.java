package faang.school.urlshortenerservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class TaskExecutorConfig {
    private final HashCacheExecutorParams hashCacheExecutorParams;
    private final HashGeneratorExecutorParams hashGeneratorExecutorParams;

    @Bean("hashCacheExecutor")
    public ThreadPoolTaskExecutor hashCacheExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(hashCacheExecutorParams.getCorePoolSize());
        taskExecutor.setMaxPoolSize(hashCacheExecutorParams.getMaxPoolSize());
        taskExecutor.setQueueCapacity(hashCacheExecutorParams.getQueueCapacity());
        taskExecutor.setThreadNamePrefix(hashCacheExecutorParams.getThreadNamePrefix());

        return taskExecutor;
    }

    @Bean("hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(hashGeneratorExecutorParams.getCorePoolSize());
        taskExecutor.setMaxPoolSize(hashGeneratorExecutorParams.getMaxPoolSize());
        taskExecutor.setQueueCapacity(hashGeneratorExecutorParams.getQueueCapacity());
        taskExecutor.setThreadNamePrefix(hashGeneratorExecutorParams.getThreadNamePrefix());

        return taskExecutor;
    }
}
