package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {
    @Value("${thread-pool.pool-size.core}")
    private int corePoolSize;

    @Value("${thread-pool.pool-size.max}")
    private int maxPoolSize;

    @Value("${thread-pool.queue}")
    private int queueCapacity;

    @Bean(name = "HashGeneratorPool")
    public Executor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}
