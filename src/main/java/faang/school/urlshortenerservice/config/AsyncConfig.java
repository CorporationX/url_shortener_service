package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${app.hash-generator.core-pool-size:5}")
    private int hashGeneratorCorePoolSize;

    @Value("${app.hash-generator.max-pool-size:10}")
    private int hashGeneratorMaxPoolSize;

    @Value("${hash-cache.core-pool-size:5}")
    private int hashCacheCorePoolSize;

    @Value("${hash-cache.max-pool-size:10}")
    private int hashCacheMaxPoolSize;

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCacheCorePoolSize);
        executor.setMaxPoolSize(hashCacheMaxPoolSize);
        executor.setThreadNamePrefix("HashCache-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorCorePoolSize);
        executor.setMaxPoolSize(hashGeneratorMaxPoolSize);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}