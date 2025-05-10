package faang.school.urlshortenerservice.config.app;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    private final HashConfig hashConfig;

    @Bean(name = "hashGeneratorTaskExecutor")
    public ThreadPoolTaskExecutor hashGeneratorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashConfig.getThreadPoolSize());
        executor.setMaxPoolSize(hashConfig.getThreadPoolSize());
        executor.setQueueCapacity(hashConfig.getThreadPoolQueueSize());
        executor.setThreadNamePrefix("HashGen-");
        executor.initialize();
        return executor;
    }
}
