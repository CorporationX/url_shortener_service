package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashThreadPoolConfig {
    @Value("${thread-pool.size}")
    private int poolSize;
    @Value("${thread-pool.queue_capacity}")
    private int queueCapacity;

//    @Bean
//    public Executor hashThreadPool() {
//        return Executors.newFixedThreadPool(poolSize);
//    }

    @Bean
    public ThreadPoolTaskExecutor hashThreadPool() {
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(poolSize);
        taskExecutor.setQueueCapacity(queueCapacity);

        return taskExecutor;
    }
}
