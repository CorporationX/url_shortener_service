package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${hash.generator.thread.pool.size:5}")
    private int threadPoolSize;

    @Value("${hash.generator.thread.pool.queue.capacity:100}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setMaxPoolSize(threadPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-gen-");
        executor.initialize();
        return executor;
    }
}