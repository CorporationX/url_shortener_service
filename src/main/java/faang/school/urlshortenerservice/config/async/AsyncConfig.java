package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final AsyncProperties properties;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.threadPoolSize());
        executor.setMaxPoolSize(properties.threadPoolSize());
        executor.setQueueCapacity(properties.threadQueueCapacity());
        executor.setThreadNamePrefix(properties.threadNamePrefix());
        executor.initialize();
        return executor;
    }

}
