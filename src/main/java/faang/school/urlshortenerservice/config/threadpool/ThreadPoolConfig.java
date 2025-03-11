package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Value("${thread_pool.encode.pool_size:5}")
    private int encoderPoolSize;

    @Bean
    public ExecutorService encodeThreadPool() {
        return Executors.newFixedThreadPool(encoderPoolSize);
    }
}
