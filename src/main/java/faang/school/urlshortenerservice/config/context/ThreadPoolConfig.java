package faang.school.urlshortenerservice.config.context;


import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Setter
@Configuration
@ConfigurationProperties(prefix = "hash-service.thread-pool")
public class ThreadPoolConfig {
    private int poolSize;
    private int queueSize;

    @Bean
    public ThreadPoolTaskExecutor poolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setQueueCapacity(queueSize);
        taskExecutor.setCorePoolSize(poolSize);
        return taskExecutor;
    }
}
