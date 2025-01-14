package faang.school.urlshortenerservice.config.threadpool;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class HashGeneratorThreadPoolConfig {
    @Value("${app.hash_generator_thread_pool.core_size:2}")
    private int coreSize;
    @Value("${app.hash_generator_thread_pool.max_size:16}")
    private int maxSize;
    @Value("${app.hash_generator_thread_pool.keep_alive_millisecond:5000}")
    private long keepAliveTime;
    @Value("${app.hash_generator_thread_pool.queue_size:10000}")
    private int queueSize;
    BlockingQueue<Runnable> workQueue;

    @PostConstruct
    public void initWorkQueue() {
        this.workQueue = new ArrayBlockingQueue<>(queueSize);
    }

    @Bean
    public ExecutorService hashGeneratorThreadPool() {
        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
