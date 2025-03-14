package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Value("${hash.thread.size}")
    private int hashThreadPoolSize;

    @Value("${hash.cache.thread.size}")
    private int hashCacheThreadPoolSize;

    @Bean
    public ExecutorService hashThreadPool() {
        return Executors.newFixedThreadPool(hashThreadPoolSize);
    }

    @Bean
    public ExecutorService hashCacheThreadPool() {
        return Executors.newFixedThreadPool(hashCacheThreadPoolSize);
    }
}