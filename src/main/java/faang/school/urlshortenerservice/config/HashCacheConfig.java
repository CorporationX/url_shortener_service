package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class HashCacheConfig {

    @Value("${hash.cache.executor.core-size:3}")
    private int corePoolSize;

    @Value("${hash.cache.executor.max-size:5}")
    private int maxPoolSize;

    @Value("${hash.cache.executor.queue-capacity:50}")
    private int queueCapacity;

    @Bean(name = "hashCacheExecutor")
    public ExecutorService hashCacheExecutor() {
        log.info("Creating hashCacheExecutor with corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                corePoolSize, maxPoolSize, queueCapacity);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("hash-cache-");
                    return thread;
                }
        );

        executor.allowCoreThreadTimeOut(true);

        return executor;
    }
}