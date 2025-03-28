package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Value("${executor.core-pool-size:2}")
    private int corePoolSize;

    @Value("${executor.max-pool-size:4}")
    private int maxPoolSize;

    @Value("${executor.queue-capacity:50}")
    private int queueCapacity;

    @Value("${executor.queue-keep-alive-time:60}")
    private Long keepAlive;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAlive,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
