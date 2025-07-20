package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final AsyncProperties asyncProperties;

    @Bean("hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.getThreadPoolSize());
        executor.setMaxPoolSize(asyncProperties.getThreadPoolSize());
        executor.setQueueCapacity(asyncProperties.getThreadPoolQueueCapacity());
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}
