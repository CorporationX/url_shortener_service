package faang.school.urlshortenerservice.config.context.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfig {

    @Value("${spring.url.hash.cache.pool-size}")
    private int poolSize;

    @Bean
    public ExecutorService hashExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
