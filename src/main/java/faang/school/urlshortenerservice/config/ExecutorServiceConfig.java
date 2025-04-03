package faang.school.urlshortenerservice.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorServiceConfig {

    @Value("${executor.corePoolSize}")
    private int corePoolSize;

    @Value("${executor.maxPoolSize}")
    private int maxPoolSize;

    @Value("${executor.queueCapacity}")
    private int queueCapacity;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueCapacity)
        );
    }
}
