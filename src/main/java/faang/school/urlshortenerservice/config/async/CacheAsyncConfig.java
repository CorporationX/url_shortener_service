package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class CacheAsyncConfig {

    @Value("${hash.cache.async.core-pool-size}")
    private int corePoolSize;
    @Value("${hash.cache.async.max-pool-size}")
    private int maxPoolSize;
    @Value("${hash.cache.async.queue-capacity}")
    private int queueCapacity;
    @Value("${hash.cache.async.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor cacheThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }
}
