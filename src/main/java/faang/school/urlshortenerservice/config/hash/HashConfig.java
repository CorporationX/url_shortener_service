package faang.school.urlshortenerservice.config.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashConfig {
    @Value("${hash.thread-pool.size.min}")
    private Integer HASH_THREAD_POOL_MIN_SIZE;
    @Value("${hash.thread-pool.size.max}")
    private Integer HASH_THREAD_POOL_MAX_SIZE;
    @Value("${hash.thread-pool.keep-alive.time}")
    private Integer HASH_THREAD_POOL_ALIVE_TIME;
    @Value("${hash.thread-pool.keep-alive.time-unit}")
    private TimeUnit HASH_THREAD_POOL_ALIVE_TIME_UNIT;
    @Value("${hash.thread-pool.queue.size}")
    private Integer HASH_THREAD_POOL_QUEUE_SIZE;

    @Bean
    public ThreadPoolExecutor hashThreadPool() {
        return new ThreadPoolExecutor(HASH_THREAD_POOL_MIN_SIZE,
                HASH_THREAD_POOL_MAX_SIZE,
                HASH_THREAD_POOL_ALIVE_TIME,
                HASH_THREAD_POOL_ALIVE_TIME_UNIT,
                new LinkedBlockingQueue<>(HASH_THREAD_POOL_QUEUE_SIZE));
    }
}
