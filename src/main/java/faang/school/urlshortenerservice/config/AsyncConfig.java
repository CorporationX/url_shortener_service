package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${url-shortener.async.executor.core-pool-size}")
    private int corePoolSize;
    
    @Value("${url-shortener.async.executor.max-pool-size}")
    private int maxPoolSize;
    
    @Value("${url-shortener.async.executor.queue-capacity}")
    private int queueCapacity;
    
    @Value("${url-shortener.async.executor.thread-name-prefix}")
    private String threadNamePrefix;
    
    @Value("${url-shortener.async.executor.await-termination-seconds}")
    private int awaitTerminationSeconds;
    
    @Value("${url-shortener.async.executor.wait-for-tasks-to-complete-on-shutdown}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}


