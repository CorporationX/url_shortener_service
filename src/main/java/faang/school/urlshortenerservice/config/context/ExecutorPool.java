package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorPool {
    @Value("${executor_pool.size:10}")
    private int poolSize;
    @Value("${executor_pool.core:5}")
    private int corePoolSize;
    @Value("${executor_pool.queue_capacity:50}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor urlShortenerPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
