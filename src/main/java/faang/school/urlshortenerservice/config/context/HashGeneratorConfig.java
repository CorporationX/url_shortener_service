package faang.school.urlshortenerservice.config.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Data
@Configuration
public class HashGeneratorConfig {
    @Value("${url-shortener-service.hash-generator.unique-batch}")
    private int uniqueBatch;
    @Value("${url-shortener-service.hash-generator.thread-pool-size}")
    private int threadPoolSize;
    @Value("${url-shortener-service.hash-generator.thread-queue}")
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
