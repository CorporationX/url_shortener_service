package faang.school.urlshortenerservice.config.hashGenerator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Data
@Configuration
@ConfigurationProperties(prefix = "hashgenerator")
public class HashGeneratorConfig {

    private int batchSize;
    private int threadpoolSize;
    private int threadpoolQueueCapacity;

    private String base62Alphabet;

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadpoolSize);
        executor.setMaxPoolSize(threadpoolSize);
        executor.setQueueCapacity(threadpoolQueueCapacity);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}
