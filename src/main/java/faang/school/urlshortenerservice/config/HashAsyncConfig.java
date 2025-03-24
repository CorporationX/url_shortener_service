package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class HashAsyncConfig {

    @Value("${hash.generator.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${hash.generator.thread-pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${hash.generator.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Value("${hash.generator.thread-pool.keep-alive-seconds}")
    private int keepAliveSeconds;


    @Value("${hash.cache.thread-pool.core-pool-size}")
    private int cacheCorePoolSize;

    @Value("${hash.cache.thread-pool.max-pool-size}")
    private int cacheMaxPoolSize;

    @Value("${hash.cache.thread-pool.queue-capacity}")
    private int cacheQueueCapacity;

    @Value("${hash.cache.thread-pool.keep-alive-seconds}")
    private int cacheKeepAliveSeconds;

    @Bean(name = "hashGeneratorThreadPool")
    public Executor hashGeneratorExecutor() {
        log.info("Initializing hashGeneratorThreadPool: corePoolSize={}, maxPoolSize={}, " +
                        "queueCapacity={}, keepAliveSeconds={}",
                corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashCacheThreadPool")
    public Executor hashCacheThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cacheCorePoolSize);
        executor.setMaxPoolSize(cacheMaxPoolSize);
        executor.setQueueCapacity(cacheQueueCapacity);
        executor.setKeepAliveSeconds(cacheKeepAliveSeconds);
        executor.setThreadNamePrefix("HashCache-");
        executor.initialize();
        return executor;
    }
}