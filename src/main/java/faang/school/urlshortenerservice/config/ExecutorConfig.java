package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Value("${executor.hash-generator.core-size}")
    private int hashGeneratorCorePoolSize;

    @Value("${executor.hash-generator.max-size}")
    private int hashGeneratorMaxPoolSize;

    @Value("${executor.hash-generator.queue-capacity}")
    private int hashGeneratorQueueCapacity;

    @Value("${executor.hash-generator.prefix}")
    private String hashGeneratorPrefix;

    @Value("${executor.hash-cache.core-size}")
    private int hashCacheCorePoolSize;

    @Value("${executor.hash-cache.max-size}")
    private int hashCacheMaxPoolSize;

    @Value("${executor.hash-cache.queue-capacity}")
    private int hashCacheQueueCapacity;

    @Value("${executor.hash-cache.prefix}")
    private String hashCachePrefix;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorCorePoolSize);
        executor.setMaxPoolSize(hashGeneratorMaxPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.setThreadNamePrefix(hashGeneratorPrefix);
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCacheCorePoolSize);
        executor.setMaxPoolSize(hashCacheMaxPoolSize);
        executor.setQueueCapacity(hashCacheQueueCapacity);
        executor.setThreadNamePrefix(hashCachePrefix);
        executor.initialize();
        return executor;
    }
}