package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * @author Alexander Bulgakov
 */

@Configuration
public class AsyncConfiguration implements AsyncConfigurer {
    @Value("${executor.settings.max_pool_size}")
    private int maxPoolSize;

    @Value("${executor.settings.core_pool_size}")
    private int corePoolSize;

    @Value("${executor.settings.queue_capacity}")
    private int queueCapacity;

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();

        return executor;
    }
}
