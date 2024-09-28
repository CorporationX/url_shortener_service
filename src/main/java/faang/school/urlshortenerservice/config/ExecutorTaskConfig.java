package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "task-executor")
@Slf4j
public class ExecutorTaskConfig {
    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.thread.pool.core}")
    private int corePoolSize;

    @Value("${hash.thread.pool.max}")
    private int maximumPoolSize;

    @Value("${hash.thread.pool.alive.time}")
    private int keepAliveTime;


    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        log.info("Creating executor service");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setQueueCapacity(capacity);
        executor.initialize();

        return executor;
    }
}
