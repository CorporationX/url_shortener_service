package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import faang.school.urlshortenerservice.config.properties.HashProperties;
import jakarta.validation.constraints.NotNull;
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
    private final CacheProperties cacheProperties;

    @Bean(name = "hashGeneratorExecutor")
    public Executor taskExecutor() {
        return getExecutor(hashProperties.corePoolSize(), hashProperties.maxPoolSize(), hashProperties.queueCapacity(), hashProperties.threadNamePrefix());
    }

    @Bean(name = "cachePoolExecutor")
    public Executor cacheThreadPool() {
        return getExecutor(cacheProperties.corePoolSize(), cacheProperties.maxPoolSize(), cacheProperties.queueCapacity(), cacheProperties.threadNamePrefix());
    }

    @NotNull
    private Executor getExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
