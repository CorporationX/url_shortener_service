package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncPoolsConfig {
    @Value("${hash.generator.thread-pool-size}")
    private int hashGeneratorPoolSize;

    @Bean
    public ExecutorService hashGeneratorPool() {
        return Executors.newFixedThreadPool(hashGeneratorPoolSize);
    }
}
