package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Value("${async.pool.core-size}")
    private int corePoolSize;

    @Value("${async.pool.max-size}")
    private int maxPoolSize;

    @Value("${async.pool.queue-capacity}")
    private int queueCapacity;

    @Value("${async.pool.keep-alive-time}")
    private Long keepAliveTime;

    @Bean
    public ExecutorService hashCacheExecutor() {
        return new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
