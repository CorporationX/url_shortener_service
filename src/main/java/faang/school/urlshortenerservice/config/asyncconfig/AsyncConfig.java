package faang.school.urlshortenerservice.config.asyncconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${async.pool.core-size}")
    private int corePoolSize;

    @Value("${async.pool.max-size}")
    private int maxPoolSize;

    @Value("${async.pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashExecutor")
    public Executor hashExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-gen-");
        executor.initialize();
        return executor;
    }
}
