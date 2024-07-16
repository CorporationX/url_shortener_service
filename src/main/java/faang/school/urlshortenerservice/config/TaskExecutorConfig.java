package faang.school.urlshortenerservice.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Setter
@Configuration
@ConfigurationProperties(prefix = "task-executor")
public class TaskExecutorConfig {
    private int poolSize;
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(poolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        return taskExecutor;
    }
}