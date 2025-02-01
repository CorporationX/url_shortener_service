package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {
    @Value("${hash.threadpool.size}")
    private int poolSize;
    @Value("${hash.threadpool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "executorHashGenerator")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("custom-thread-");
        executor.initialize();
        return executor;
    }
}
