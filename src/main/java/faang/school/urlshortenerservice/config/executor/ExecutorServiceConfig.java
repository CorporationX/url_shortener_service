package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorServiceConfig {

    @Value("${executor.thread-pool.size}")
    private int threadPoolSize;

    @Value("${executor.queue.capacity}")
    private int queueCapacity;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                threadPoolSize,
                threadPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity));
    }
}
