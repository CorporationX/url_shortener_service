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
    private Integer HASH_CACHE_THREAD_POOL_MIN_SIZE;
    @Value("${hash.cache.thread-pool.size.max}")
    private Integer HASH_CACHE_THREAD_POOL_MAX_SIZE;
    @Value("${hash.cache.thread-pool.keep-alive.time}")
    private Integer HASH_CACHE_THREAD_POOL_ALIVE_TIME;
    @Value("${hash.cache.thread-pool.keep-alive.time-unit}")
    private TimeUnit HASH_CACHE_THREAD_POOL_ALIVE_TIME_UNIT;
    @Value("${hash.cache.thread-pool.queue.size}")
    private Integer HASH_CACHE_THREAD_POOL_QUEUE_SIZE;

    @Value("${hash.cache.queue.size}")
    private Integer HASH_QUEUE_SIZE;

    @Bean
    public ThreadPoolExecutor hashCacheThreadPool() {
        return new ThreadPoolExecutor(HASH_CACHE_THREAD_POOL_MIN_SIZE,
                HASH_CACHE_THREAD_POOL_MAX_SIZE,
                HASH_CACHE_THREAD_POOL_ALIVE_TIME,
                HASH_CACHE_THREAD_POOL_ALIVE_TIME_UNIT,
                new LinkedBlockingQueue<>(HASH_CACHE_THREAD_POOL_QUEUE_SIZE));
    }

    @Bean
    public ArrayBlockingQueue<String> hashCacheQueue() {
        return new ArrayBlockingQueue<>(HASH_QUEUE_SIZE);
    }
}
