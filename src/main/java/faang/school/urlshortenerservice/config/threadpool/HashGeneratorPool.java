package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class HashGeneratorPool {

    @Value("${thread_pool.hash_generator_size}")
    private int hashGeneratorPoolSize;

    @Bean
    public ExecutorService hashGeneratorThreadPool() {
        return Executors.newFixedThreadPool(hashGeneratorPoolSize);
    }
}