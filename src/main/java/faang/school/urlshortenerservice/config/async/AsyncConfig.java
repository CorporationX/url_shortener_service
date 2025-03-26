package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Value("${hash.threadPool.core-pool-size}")
    private int corePoolSize;

    @Value("${hash.threadPool.max-pool-size}")
    private int maxPoolSize;

    @Value("${hash.threadPool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "customThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor customThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-generator-");
        executor.initialize();
        return executor;
    }
}
