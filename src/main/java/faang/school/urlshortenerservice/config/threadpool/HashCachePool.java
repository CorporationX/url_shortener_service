package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class HashCachePool {

    @Value("thread_pool.hash_cache_size")
    private int poolSize;

    @Bean
    public ExecutorService hashCachePool() {
        return Executors.newFixedThreadPool(poolSize);
    }
}