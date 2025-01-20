package faang.school.urlshortenerservice.config.context.faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Value("${thread.pool.core-pool-size}")
    private int corePoolSize;

    @Value("${thread.pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${thread.pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-Thread-HashGenerator");
        executor.initialize();
        return executor;
    }
}
