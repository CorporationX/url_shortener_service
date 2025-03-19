package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${url.cleanup.thread-pool-size:4}")
    private int urlCleanupThreadPoolSize;

    @Value("${hash.cleanup.thread-pool-size:5}")
    private int hashCleanupThreadPoolSize;

    @Bean
    public ExecutorService urlCleanupExecutor() {
        return Executors.newFixedThreadPool(urlCleanupThreadPoolSize);
    }

    @Bean
    public Executor hashThreadExecutor(){
        return Executors.newFixedThreadPool(hashCleanupThreadPoolSize);
    }
} 