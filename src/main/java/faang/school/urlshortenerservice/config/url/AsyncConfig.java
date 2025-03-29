package faang.school.urlshortenerservice.config.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${data.thread.pool.core-pool-size}")
    private int corePoolSize;

    @Value("${data.thread.pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${data.thread.pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hashGeneratorExecutor-");
        executor.initialize();
        return executor;
    }
}
