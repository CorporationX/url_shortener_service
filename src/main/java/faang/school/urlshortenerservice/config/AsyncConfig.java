package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.properties.HashCacheFillAsyncProperties;
import faang.school.urlshortenerservice.properties.HashGeneratorAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final HashGeneratorAsyncProperties hashGenerator;
    private final HashCacheFillAsyncProperties hashCacheFill;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(hashGenerator.getThreadNamePrefix());
        executor.setCorePoolSize(hashGenerator.getCorePoolSize());
        executor.setMaxPoolSize(hashGenerator.getMaxPoolSize());
        executor.setQueueCapacity(hashGenerator.getQueueCapacity());
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor hashCacheFillExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(hashCacheFill.getThreadNamePrefix());
        executor.setCorePoolSize(hashCacheFill.getCorePoolSize());
        executor.setMaxPoolSize(hashCacheFill.getMaxPoolSize());
        executor.setQueueCapacity(hashCacheFill.getQueueCapacity());
        executor.initialize();
        return executor;
    }
}
