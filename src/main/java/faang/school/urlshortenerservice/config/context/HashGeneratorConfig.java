package faang.school.urlshortenerservice.config.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Data
@Configuration
@ConfigurationProperties(prefix = "hash-generator")
public class HashGeneratorConfig {
    private int uniqueBatch;
    private int threadPoolSize;
    private int threadQueue;

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(threadPoolSize);
        executor.setQueueCapacity(threadQueue);
        executor.initialize();
        return executor;
    }
}