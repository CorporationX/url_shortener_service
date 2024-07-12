package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Value("${config.thread-pool.size}")
    private int poolSize;

    @Value("${config.queue.size}")
    private int queueSize;

    @Bean
    public ExecutorService getThreadPool() {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(queueSize));
    }
}
