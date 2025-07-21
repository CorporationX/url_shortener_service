package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(HashCacheProperties.class)
@RequiredArgsConstructor
public class HashCacheConfig {
    @Value("${executor.pool-size}")
    private int poolSize;
    @Value("${executor.queue-size}")
    private int queueSize;

    @Bean
    public ExecutorService hashCacheExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L,
                java.util.concurrent.TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.prestartAllCoreThreads();
        return executor;
    }

}
