package faang.school.urlshortenerservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashCacheThreadPoolConfig {

    @Value("${hash.cache.executor.pool-size}")
    private int poolSize;

    @Value("${hash.cache.executor.queue-size}")
    private int queueSize;

    @Bean("hashCacheThreadPool")
    public ExecutorService hashCacheExecutor() {
        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
