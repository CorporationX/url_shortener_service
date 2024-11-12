package faang.school.urlshortenerservice.config.async;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    private final AsyncProperty asyncProperty;

    @Bean
    public ThreadPoolTaskExecutor executorToGenerateHashes() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperty.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperty.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperty.getQueueCapacity());
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
