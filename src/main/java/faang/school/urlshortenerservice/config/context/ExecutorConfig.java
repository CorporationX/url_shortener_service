package faang.school.urlshortenerservice.config.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Data
@ConfigurationProperties(prefix = "spring.task.execution.pool")
public class ExecutorConfig implements AsyncConfigurer {

    @Value("${executor.service.batch_size}")
    private int threadBatch;

    private int coreSize;
    private int maxSize;
    private int queueCapacity;
    private String threadNamePrefix;


    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize); // Minimum number of threads
        executor.setMaxPoolSize(maxSize); // Maximum number of threads
        executor.setQueueCapacity(queueCapacity); // Size of the waiting queue
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

}
