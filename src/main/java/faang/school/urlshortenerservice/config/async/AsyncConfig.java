package faang.school.urlshortenerservice.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(5);
    }
}
