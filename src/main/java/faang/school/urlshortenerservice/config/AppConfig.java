package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Bean(name = "hashGeneratorThreadPool")
    public ExecutorService hashGeneratorThreadPool(
            @Value("${hash-generator.thread-pool.pool-size:10}") int poolSize,
            @Value("${hash-generator.thread-pool.queue-capacity:10}") int queueCapacity,
            @Value("${hash-generator.thread-pool.keep-alive:60}") int keepAlive

    ) {
        return new ThreadPoolExecutor(
                poolSize, poolSize,
                keepAlive, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Primary
    @Bean(name = "hashCacheThreadPool")
    public ExecutorService hashCacheThreadPool(
            @Value("${hash-cache.thread-pool.pool-size:10}") int poolSize,
            @Value("${hash-cache.thread-pool.queue-capacity:10}") int queueCapacity,
            @Value("${hash-cache.thread-pool.keep-alive:60}") int keepAlive
    ) {
        return new ThreadPoolExecutor(
                poolSize, poolSize,
                keepAlive, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}