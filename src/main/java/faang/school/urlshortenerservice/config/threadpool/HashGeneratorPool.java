package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class HashGeneratorPool {

    @Value("${thread_pool.hash_generator_size}")
    private int HASH_GENERATOR_POOL_SIZE;

    @Bean
    public ExecutorService hashGeneratorThreadPool() {
        return Executors.newFixedThreadPool(HASH_GENERATOR_POOL_SIZE);
    }
}
