package faang.school.urlshortenerservice.config.context.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${spring.thread.pool.core-pool-size}")
    private int corePoolSize;

    @Value("${spring.thread.pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${spring.thread.pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGen-");
        executor.initialize();
        return executor;
    }
}
