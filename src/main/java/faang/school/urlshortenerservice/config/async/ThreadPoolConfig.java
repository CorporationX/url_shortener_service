package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Value("${async.core}")
    public int corePoolSize;
    @Value("${async.max}")
    public int maxPoolSize;
    @Value("${async.queue_capacity}")
    public int queueCapacity;

    @Bean(name = "generateBatchExecutor")
    public ThreadPoolTaskExecutor threadPoolForGenerateBatch() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("generateBatchExecutor-");
        executor.initialize();
        return executor;
    }
}