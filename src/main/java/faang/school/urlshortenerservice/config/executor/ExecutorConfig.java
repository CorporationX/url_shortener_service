package faang.school.urlshortenerservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private final ExecutorParams executorParams;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorParams.getCorePoolSize());
        executor.setMaxPoolSize(executorParams.getMaxPoolSize());
        executor.setQueueCapacity(executorParams.getQueueCapacity());
        executor.initialize();
        return executor;
    }
}
