package faang.school.urlshortenerservice.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService hashCacheExecutorService() {
        int corePoolSize = 4; // Читается из конфига
        int maxPoolSize = 10; // Читается из конфига
        int queueCapacity = 100; // Читается из конфига

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
    }
}