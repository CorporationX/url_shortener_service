package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.config.properties.HashCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class HashCacheExecutorConfig {

    private final HashCacheProperties hashCacheProperties;

    @Bean(name = "hashCacheExecutor")
    public TaskExecutor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCacheProperties.getThreadPool().getCoreSize());
        executor.setMaxPoolSize(hashCacheProperties.getThreadPool().getMaxSize());
        executor.setQueueCapacity(hashCacheProperties.getThreadPool().getQueueCapacity());
        executor.setThreadNamePrefix("HashCache-");
        executor.initialize();
        return executor;
    }
}
