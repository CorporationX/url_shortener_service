package faang.school.urlshortenerservice.config.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class GenerateHashesThreadPool {
    @Value("${hash.hash-generation-thread-pool-size}")
    private int generateHashesThreadPoolSize;

    @Bean
    public ExecutorService hashesThreadPool() {
        return Executors.newFixedThreadPool(generateHashesThreadPoolSize);
    }
}
