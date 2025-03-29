package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class SpringAsyncConfig {

    @Value("${hash.generate.thread-count}")
    private int generateThreadCount;

    @Value("${hash.cache.thread-count}")
    private int hashCacheThreadCount;

    @Value("${hash.save.thread-count}")
    private int saveHashThreadCount;

    @Value("${hash.batch.size}")
    private int batchSize;

    @Bean(name = "generateHashExecutor")
    public Executor generateHashExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generateThreadCount);
        executor.setMaxPoolSize(generateThreadCount * 2);
        executor.setQueueCapacity(batchSize);
        executor.setThreadNamePrefix("Generate-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "saveHashExecutor")
    public Executor saveHashExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(saveHashThreadCount);
        executor.setMaxPoolSize(saveHashThreadCount * 2);
        executor.setQueueCapacity(batchSize);
        executor.setThreadNamePrefix("Save-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCacheThreadCount);
        executor.setMaxPoolSize(hashCacheThreadCount * 2);
        executor.setQueueCapacity(batchSize);
        executor.setThreadNamePrefix("Cache-");
        executor.initialize();
        return executor;
    }
}
