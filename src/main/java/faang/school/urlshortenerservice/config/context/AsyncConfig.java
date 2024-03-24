package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Value("${async.generator-thread-pool.core-pool-size}")
    private int generatorCorePoolSize;
    @Value("${async.generator-thread-pool.max-pool-size}")
    private int generatorMaxPoolSize;
    @Value("${async.generator-thread-pool.queue-capacity}")
    private int generatorQueueCapacity;

    @Bean(name = "generateBatchThreadPool")
    public TaskExecutor generateBatchThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(generatorCorePoolSize);
        executor.setMaxPoolSize(generatorMaxPoolSize);
        executor.setQueueCapacity(generatorQueueCapacity);

        executor.initialize();
        return executor;
    }
}