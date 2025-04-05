package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService hashCacheExecutor(
            @Value("${spring.hash-cache.executor.core-pool-size}") int corePoolSize,
            @Value("${spring.hash-cache.executor.max-pool-size}") int maximumPoolSize,
            @Value("${spring.hash-cache.executor.queue-capacity}") int queueCapacity) {

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
