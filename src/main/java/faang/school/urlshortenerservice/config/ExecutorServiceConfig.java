package faang.school.urlshortenerservice.config;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorServiceConfig {
    @Value("${thread_pool.core_pool_size}")
    private int corePoolSize;

    @Value("${thread_pool.max_pool_size}")
    private int maxPoolSize;
    @Value("${thread_pool.queue_capacity}")
    private int queueCapacity;


    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
    }
}
