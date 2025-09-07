package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorServiceConfig {
    @Value("${hash.executor.core-pool-size}")
    private int corePoolSize;
    @Value("${hash.executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${hash.executor.queue-capacity}")
    private int queueCapacity;

    @Bean("hashGeneratorExecutorService")
    public Executor hashGeneratorExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}