package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    @Value("${spring.properties.thread-pool-size}")
    private int THREAD_POOL_SIZE;

    @Bean
    public ExecutorService customThreadPool() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }
}
