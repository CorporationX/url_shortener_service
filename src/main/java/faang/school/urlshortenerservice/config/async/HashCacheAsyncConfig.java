package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashCacheAsyncConfig {

    @Bean(name = "hashCacheThreadPool")
    public ExecutorService executorService(
            @Value("${url.hash.thread-pool.size}") int corePoolSize,
            @Value("${url.hash.thread-pool.max-size}") int maxPoolSize,
            @Value("${url.hash.thread-pool.ttl}") int keepAliveTime,
            @Value("${url.hash.thread-pool.queue}") int queueCapacity
    ) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
