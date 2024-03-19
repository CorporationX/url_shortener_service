package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {
    @Value("${thread-pool.size}")
    private int threadPoolSize;
    @Value("${thread-pool.queue}")
    private int threadPoolQueue;

    @Bean
    public ThreadPoolTaskExecutor treadPool() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setQueueCapacity(threadPoolQueue);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
