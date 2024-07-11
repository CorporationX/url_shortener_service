package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashConfig {

    @Value("${url.hash.pool.size}")
    private int poolSize;

    @Value("${url.hash.pool.queue}")
    private int poolQueueSize;

    @Bean
    public ExecutorService hashGenPool() {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(poolQueueSize));
    }
}
