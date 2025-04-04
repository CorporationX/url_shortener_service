package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Value("${AsyncConfig.corePoolSize}")
    private int corePoolSize;

    @Value("${AsyncConfig.maxPoolSize}")
    private int maxPoolSize;

    @Value("${AsyncConfig.queueCapacity}")
    private int queueCapacity;

    @Bean(name = "hashTaskExecutor")
    public ThreadPoolTaskExecutor hashTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashExecutor-");
        executor.initialize();
        return executor;
    }
}