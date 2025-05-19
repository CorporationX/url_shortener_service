package faang.school.urlshortenerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService getHashesPool() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService saveHashesPool() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService generateHashPool() {
        return Executors.newSingleThreadExecutor();
    }
}
