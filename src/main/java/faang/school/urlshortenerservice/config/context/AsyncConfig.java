package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${hash.executor.core-size}")
    private int corePoolSize;

    @Bean(name = "hashGeneratorTaskExecutor")
    public ExecutorService hashGeneratorTaskExecutor() {
        return Executors.newFixedThreadPool(corePoolSize);
    }

    @Bean(name = "hashCacheTaskExecutor")
    public ExecutorService hashCacheTaskExecutor() {
        return Executors.newFixedThreadPool(corePoolSize);
    }
}