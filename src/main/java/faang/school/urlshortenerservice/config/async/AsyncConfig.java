package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;

@Configuration
public class AsyncConfig {

    @Value("${async.core-pool-size}")
    private int corePoolSize;
    @Value("${async.max-pool-size}")
    private int maxPoolSize;
    @Value("${async.queue-capacity}")
    private int queueCapacity;
    @Value("${async.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean
    public ExecutorService asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

}