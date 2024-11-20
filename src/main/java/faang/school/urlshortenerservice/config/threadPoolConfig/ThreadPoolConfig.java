package faang.school.urlshortenerservice.config.threadPoolConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Value("${executor.cores}")
    public int corePoolSize;
    @Value("${executor.max}")
    public int maxPoolSize;
    @Value("${executor.queue_capacity}")
    public int queueCapacity;

    @Bean(name = "generateHashesExecutor")
    public ThreadPoolTaskExecutor threadPoolForGenerateBatch() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

    @Bean(name = "generateBatchExecutor")
    public ThreadPoolTaskExecutor generateBatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}