package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread-pool.pool-size}")
    private int POOL_SIZE;

    @Value("${thread-pool.queue-size}")
    private int TASK_QUEUE_SIZE;

    @Bean
    public Executor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_SIZE);
        executor.initialize();
        return executor;
    }

}
