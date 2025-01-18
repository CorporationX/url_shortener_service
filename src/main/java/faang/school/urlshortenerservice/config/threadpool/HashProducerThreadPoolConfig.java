package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class HashProducerThreadPoolConfig {
    @Value("${hash.thread-pool.producer.core-pool-size}")
    private int corePoolSize;
    @Value("${hash.thread-pool.producer.max-pool-size}")
    private int maxPoolSize;
    @Value("${hash.thread-pool.producer.queue-capacity}")
    private int queueCapacity;

    @Bean
    public Executor hashProducerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
