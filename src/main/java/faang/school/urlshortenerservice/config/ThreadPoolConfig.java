package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Value("${spring.threads.pool.number}")
    private int threads;

    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(threads);
    }
}