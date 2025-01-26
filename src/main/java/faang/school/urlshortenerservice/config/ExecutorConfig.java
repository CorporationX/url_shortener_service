package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Value("${hash.cache.executor.pool-size:5}")
    private int poolSize;

    @Value("${hash.cache.executor.queue-size:50}")
    private int queueSize;

    @Bean
    public ExecutorService hashExecutorService() {
        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}