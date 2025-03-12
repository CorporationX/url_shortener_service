package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashConfig {

    @Value("${spring.hash-generator.pool-size}")
    private static int poolSize;

    @Value("${spring.hash-generator.max-pool-size}")
    private static int maxPoolSize;

    @Value("${spring.hash-generator.queue-capacity}")
    private static int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor generateBatchThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}
