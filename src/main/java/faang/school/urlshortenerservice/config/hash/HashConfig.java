package faang.school.urlshortenerservice.config.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashConfig {
    @Bean
    public ThreadPoolTaskExecutor hashGeneratorThreadPool(
            @Value("${hash-generating.thread-pool.max-size}") int maxSize,
            @Value("${hash-generating.thread-pool.core-size}") int coreSize,
            @Value("${hash-generating.thread-pool.queue-capacity}") int queueCapacity

    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxSize);
        executor.setCorePoolSize(coreSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
