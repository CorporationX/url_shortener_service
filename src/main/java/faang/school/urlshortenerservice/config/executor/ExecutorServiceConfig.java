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

    @Value("${custom.executor-service.core-pool-size}")
    private int corePoolSize;

    @Value("${custom.executor-service.max-pool-size}")
    private int maxPoolSize;

    @Value("${custom.executor-service.queue-capacity}")
    private int queueCapacity;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
    }
}

