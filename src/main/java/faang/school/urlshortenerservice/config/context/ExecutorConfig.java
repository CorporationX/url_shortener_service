package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {
    @Value("${app.local_hash_refill.hash_generator.core_pool_size}")
    private int corePoolSize;
    @Value("${app.local_hash_refill.hash_generator.max_pool_size}")
    private int maxPoolSize;
    @Value("${app.local_hash_refill.hash_generator.queue_capacity}")
    private int queueCapacity;
    @Value("${app.local_hash_refill.hash_generator.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean(name = "refillExecutor")
    public Executor refillExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}