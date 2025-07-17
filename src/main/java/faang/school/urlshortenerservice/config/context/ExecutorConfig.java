package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Value("${hash.cache.executor.corePoolSize:4}")
    private int corePoolSize;

    @Value("${hash.cache.executor.maxPoolSize:8}")
    private int maxPoolSize;

    @Value("${hash.cache.executor.queueCapacity:100}")
    private int queueCapacity;

    @Bean
    public ExecutorService hashCacheExecutorService() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}