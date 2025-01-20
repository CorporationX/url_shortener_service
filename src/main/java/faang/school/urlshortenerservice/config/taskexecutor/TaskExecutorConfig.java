package faang.school.urlshortenerservice.config.taskexecutor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class TaskExecutorConfig {
    private final TaskExecutorProperties taskExecutorProperties;

    @Bean
    public ThreadPoolTaskExecutor shortenerTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(taskExecutorProperties.corePoolSize());
        taskExecutor.setMaxPoolSize(taskExecutorProperties.maxPoolSize());
        taskExecutor.setQueueCapacity(taskExecutorProperties.queueCapacity());
        taskExecutor.setKeepAliveSeconds(taskExecutorProperties.keepAliveTime());
        taskExecutor.setThreadNamePrefix(taskExecutorProperties.threadNamePrefix());
        taskExecutor.initialize();

        return taskExecutor;
    }
}

