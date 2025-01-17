package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Value("${spring.async.core-pool-size}")
    int corePoolSize;

    @Value("${spring.async.max-pool-size}")
    int maxPoolSize;

    @Value("${spring.async.queue-capacity}")
    int queueCapacity;

    @Bean (name = "hashServiceExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("HashGenerator-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean (name = "localCacheExecutor")
    public ThreadPoolTaskExecutor localCacheExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("LocalCache-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
