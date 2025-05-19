package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {
    private final HashProperties hashProperties;

    @Bean(name = "hashGeneratorExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashProperties.corePoolSize());
        executor.setMaxPoolSize(hashProperties.maxPoolSize());
        executor.setQueueCapacity(hashProperties.queueCapacity());
        executor.setThreadNamePrefix(hashProperties.threadNamePrefix());
        executor.initialize();
        return executor;
    }
}
