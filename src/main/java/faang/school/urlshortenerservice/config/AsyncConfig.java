package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${shortener.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${shortener.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${shortener.executor.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashServiceExecutor")
    public Executor hashServiceExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
