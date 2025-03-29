package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadsConfig {

    @Bean
    public ExecutorService hashesExecutorService(
            @Value("${threads.hashes.thread-pool-size:2}")
            int poolSize
    ) {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public ExecutorService base62EncodingExecutorService(
            @Value("${threads.base62.thread-pool-size:10}")
            int poolSize
    ) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
