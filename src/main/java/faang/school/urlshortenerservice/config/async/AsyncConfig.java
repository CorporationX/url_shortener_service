package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${config.async.hash-generator.pool-size}")
    private int hashGeneratorPoolSize;
    @Value("${config.async.hash-generator.queue-capacity}")
    private int hashGeneratorQueueCapacity;

    @Value("${config.async.hash-cache-filler.pool-size}")
    private int hashCacheFillerPoolSize;
    @Value("${config.async.hash-cache-filler.queue-capacity}")
    private int hashCacheFillerQueueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.setThreadNamePrefix("HashGen-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashCacheFillerExecutor")
    public Executor hashCacheFillerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCacheFillerPoolSize);
        executor.setQueueCapacity(hashCacheFillerQueueCapacity);
        executor.setThreadNamePrefix("HashCacheFiller-");
        executor.initialize();
        return executor;
    }
}
