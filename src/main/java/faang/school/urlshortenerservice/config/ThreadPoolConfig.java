package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration

public class ThreadPoolConfig {
    @Value("${spring.url.cache.pool-size:100}")
    private int poolSize;

    @Bean
    public ExecutorService hashGeneratorExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
