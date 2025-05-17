package faang.school.urlshortenerservice.andreev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ExecutorConfig {
    @Value("${shortener.async.pool.size:4}") private int poolSize;
    @Value("${shortener.async.pool.max-size:10}") private int poolMaxSize;
    @Value("${shortener.async.pool.queue-capacity:100}") private int poolQueueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolMaxSize);
        executor.setQueueCapacity(poolQueueCapacity);
        executor.setThreadNamePrefix("HashGen-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
