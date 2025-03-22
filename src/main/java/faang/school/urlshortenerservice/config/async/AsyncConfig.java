package faang.school.urlshortenerservice.config.async;

import faang.school.urlshortenerservice.property.CacheProperty;
import faang.school.urlshortenerservice.property.HashGeneratorProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final HashGeneratorProperty hashGeneratorConfig;
    private final CacheProperty cacheConfig;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setThreadNamePrefix(hashGeneratorConfig.getThreadName());
        executor.setCorePoolSize(hashGeneratorConfig.getPoolSize());
        executor.setMaxPoolSize(hashGeneratorConfig.getMaxPoolSize());
        executor.setQueueCapacity(hashGeneratorConfig.getQueueCapacity());
        executor.initialize();

        return executor;
    }

    @Bean
    public Executor hashCacheFillExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setThreadNamePrefix(cacheConfig.getThreadName());
        executor.setCorePoolSize(cacheConfig.getPoolSize());
        executor.setMaxPoolSize(cacheConfig.getMaxPoolSize());
        executor.setQueueCapacity(cacheConfig.getQueueCapacity());
        executor.initialize();

        return executor;
    }
}
