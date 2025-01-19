package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    private final HashGeneratorProperties hashGeneratorProperties;

    @Bean(name = "hashGeneratorTaskExecutor")
    public TaskExecutor hashGeneratorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorProperties.getThreadPool().getCoreSize());
        executor.setMaxPoolSize(hashGeneratorProperties.getThreadPool().getMaxSize());
        executor.setQueueCapacity(hashGeneratorProperties.getThreadPool().getQueueCapacity());
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}

