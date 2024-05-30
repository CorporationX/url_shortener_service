package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Value("${executorService.threadPools}")
    private int threadSize;
    @Value("${executorService.capacity}")
    private int capacity;

    @Bean
    public Executor executorService() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(threadSize);
        threadPoolTaskExecutor.setQueueCapacity(capacity);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
