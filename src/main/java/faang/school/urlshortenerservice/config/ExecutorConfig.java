package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Bean(name = "hashCacheExecutor")
    public ExecutorService hashCacheExecutor(@Value("${shortener.async.pool.size}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
