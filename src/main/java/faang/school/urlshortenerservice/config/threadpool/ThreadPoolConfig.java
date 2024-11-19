package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${hash.threads.core.pool-size}")
    private int corePoolSize;

    @Value("${hash.threads.fixed.pool-size}")
    private int fixedPoolSize;

    @Value("${hash.threads.core.max-pool}")
    private int maxPoolSize;

    @Value("${hash.threads.core.capacity}")
    private int queueCapacity;

    @Bean
    public ExecutorService generateBatchFixedThreadPool() {
        return Executors.newFixedThreadPool(fixedPoolSize);
    }

    @Bean
    public TaskExecutor taskThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();

        return executor;
    }

}
