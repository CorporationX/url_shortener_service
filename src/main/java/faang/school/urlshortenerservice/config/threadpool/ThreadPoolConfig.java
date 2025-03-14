package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Value("${thread-pool.encode.pool-size:5}")
    private int encoderPoolSize;

    @Value("${thread-pool.hash-generator.core-pool-size:5}")
    private int hashGeneratorCorePoolSize;
    @Value("${thread-pool.hash-generator.max-pool-size:25}")
    private int hashGeneratorMaxPoolSize;
    @Value("${thread-pool.hash-generator.queue-capacity:100}")
    private int hashGeneratorQueueCapacity;

    @Bean
    public ExecutorService encodeThreadPool() {
        return Executors.newFixedThreadPool(encoderPoolSize);
    }

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorCorePoolSize);
        executor.setMaxPoolSize(hashGeneratorMaxPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.initialize();
        return executor;
    }
}
