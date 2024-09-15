package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfiguration {

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.thread.pool.core}")
    private int corePoolSize;

    @Value("${hash.thread.pool.max}")
    private int maximumPoolSize;

    @Value("${hash.thread.pool.alive.time}")
    private int keepAliveTime;

    @Bean
    public BlockingQueue<String> hashes() {
        return new ArrayBlockingQueue<>(capacity);
    }

    @Bean
    public ExecutorService executorService(BlockingQueue<Runnable> queue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, queue);
    }
}
