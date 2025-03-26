package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@EnableScheduling
@Configuration
public class AsyncConfig {

    @Value("${hash.generator.thread-pool-size:5}")
    private int threadPoolSize;
    @Value("${hash.generator.queueCapacity:50}")
    private int queueCapacity;

    @Value("${hash.cash.thread-pool-size:5}")
    private int cashThreadPoolSize;

    @Value("${hash.cash.queue-capacity:50}")
    private int cashQueueCapacity;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setMaxPoolSize(threadPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerator-Thread-");
        executor.initialize();

        return executor;
    }

    @Bean
    public Executor urlCleanerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cashThreadPoolSize);
        executor.setMaxPoolSize(cashThreadPoolSize);
        executor.setQueueCapacity(cashQueueCapacity);
        executor.setThreadNamePrefix("UrlCleaner- Thread-");
        executor.initialize();

        return executor;
    }
}
