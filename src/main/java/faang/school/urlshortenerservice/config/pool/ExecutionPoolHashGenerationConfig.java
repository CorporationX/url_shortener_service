package faang.school.urlshortenerservice.config.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExecutionPoolHashGenerationConfig {
    @Value("${hash.execution-pool-parameters.core-pool-size}")
    private int corePoolSize;
    @Value("${hash.execution-pool-parameters.max-pool-capacity}")
    private int maxPoolCapacity;
    @Value("${hash.execution-pool-parameters.queue-capacity}")
    private int queueCapacity;

    @Bean("hashGenerationTaskExecutor")
    public ThreadPoolTaskExecutor hashGenerationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolCapacity);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerationThread-");
        executor.setRejectedExecutionHandler((r, exec) -> log.warn("Task rejected, thread pool is full and queue is also full"));
        executor.initialize();
        return executor;
    }
}
