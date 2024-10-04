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
    private Integer hashThreadPoolMinSize;
    @Value("${hash.thread-pool.size.max}")
    private Integer hashThreadPoolMaxSize;
    @Value("${hash.thread-pool.keep-alive.time}")
    private Integer hashThreadPoolAliveTime;
    @Value("${hash.thread-pool.keep-alive.time-unit}")
    private TimeUnit hashThreadPoolAliveTimeUnit;
    @Value("${hash.thread-pool.queue.size}")
    private Integer hashThreadPoolQueueSize;

    @Bean
    public ThreadPoolExecutor hashThreadPool() {
        return new ThreadPoolExecutor(hashThreadPoolMinSize,
                hashThreadPoolMaxSize,
                hashThreadPoolAliveTime,
                hashThreadPoolAliveTimeUnit,
                new LinkedBlockingQueue<>(hashThreadPoolQueueSize));
    }
}
