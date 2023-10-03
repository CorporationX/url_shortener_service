package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Value("${async.thread-pool.settings.core-pool-size}")
    private int corePoolSize;
    @Value("${async.thread-pool.settings.max-pool-size}")
    private int maxPoolSize;
    @Value("${async.thread-pool.settings.queue-capacity}")
    private int queueCapacity;

    @Bean("threadPoolForGenerateBatch")
    public Executor threadPoolForGenerateBatch() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
