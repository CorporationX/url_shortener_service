package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncHashGeneratorConfig {
    @Value("${hash.thread.pool-size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService asyncHashGenerator() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
