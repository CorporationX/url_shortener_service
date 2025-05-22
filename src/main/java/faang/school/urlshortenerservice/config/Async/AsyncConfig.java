package faang.school.urlshortenerservice.config.Async;

import faang.school.urlshortenerservice.dto.HashThreadPoolProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
@RequiredArgsConstructor
@EnableConfigurationProperties(HashThreadPoolProperties.class)
@Slf4j
public class AsyncConfig {
    private final HashThreadPoolProperties properties;

    @PostConstruct
    public void logThreadPoolConfig() {
        log.info("Hash Generation Thread Pool configured: coreSize={}, maxSize={}, queueCapacity={}",
                properties.coreSize(), properties.maxSize(), properties.queueCapacity());
    }

    @Bean(name = "hashGenerationExecutor")
    public Executor hashGenerationExecutor() {
        if (properties.coreSize() <= 0 || properties.maxSize() <= 0 || properties.queueCapacity() < 0) {
            throw new IllegalArgumentException("Thread pool parameters must be positive");
        }

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.coreSize());
        executor.setMaxPoolSize(properties.maxSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setThreadNamePrefix("hash-gen-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        return executor;
    }
}


