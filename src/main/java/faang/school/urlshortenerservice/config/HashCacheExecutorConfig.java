package faang.school.urlshortenerservice.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HashCacheExecutorConfig {

    @Bean(name = "hashCacheExecutor")
    public ExecutorService hashCacheExecutor(
            @Value("${hash.cache-refill.pool-size}") int poolSize,
            @Value("${hash.cache-refill.queue-capacity}") int queueCapacity
    ) {
        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
    }
}
