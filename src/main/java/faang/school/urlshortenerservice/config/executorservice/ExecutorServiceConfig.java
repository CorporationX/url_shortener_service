package faang.school.urlshortenerservice.config.executorservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Value("${hash.cache.executor.pool_size}")
    private int poolSize;

    @Bean
    public ExecutorService hashCacheExecutorService() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
