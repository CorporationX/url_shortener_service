package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final HashCacheExecutorProperties hashCacheExecutorProperties;
    private final HashGeneratorExecutorProperties hashGeneratorExecutorProperties;

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        return getExecutor(
                hashCacheExecutorProperties.getCorePoolSize(),
                hashCacheExecutorProperties.getMaxPoolSize(),
                hashCacheExecutorProperties.getQueueCapacity(),
                hashCacheExecutorProperties.getPrefix());
    }

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        return getExecutor(
                hashGeneratorExecutorProperties.getCorePoolSize(),
                hashGeneratorExecutorProperties.getMaxPoolSize(),
                hashGeneratorExecutorProperties.getQueueCapacity(),
                hashGeneratorExecutorProperties.getPrefix());
    }

    private Executor getExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();
        return executor;
    }
}
