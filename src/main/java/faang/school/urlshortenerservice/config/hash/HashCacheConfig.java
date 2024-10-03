package faang.school.urlshortenerservice.config.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashCacheConfig {
    @Value("${hash.cache.thread-pool.size.min}")
    private Integer hashCacheThreadPoolMinSize;
    @Value("${hash.cache.thread-pool.size.max}")
    private Integer hashCacheThreadPoolMaxSize;
    @Value("${hash.cache.thread-pool.keep-alive.time}")
    private Integer hashCacheThreadPoolAliveTime;
    @Value("${hash.cache.thread-pool.keep-alive.time-unit}")
    private TimeUnit hashCacheThreadPoolAliveTimeUnit;
    @Value("${hash.cache.thread-pool.queue.size}")
    private Integer hashCacheThreadPoolQueueSize;

    @Value("${hash.cache.queue.size}")
    private Integer hashQueueSize;

    @Bean
    public ThreadPoolExecutor hashCacheThreadPool() {
        return new ThreadPoolExecutor(hashCacheThreadPoolMinSize,
                hashCacheThreadPoolMaxSize,
                hashCacheThreadPoolAliveTime,
                hashCacheThreadPoolAliveTimeUnit,
                new LinkedBlockingQueue<>(hashCacheThreadPoolQueueSize));
    }

    @Bean
    public ArrayBlockingQueue<String> hashCacheQueue() {
        return new ArrayBlockingQueue<>(hashQueueSize);
    }
}
