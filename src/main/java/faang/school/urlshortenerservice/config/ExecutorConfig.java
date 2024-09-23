package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ExecutorConfig {

    @Value("${executor.hash-generator.core-size:10}")
    private int hashGeneratorCorePoolSize;

    @Value("${executor.hash-generator.max-size:20}")
    private int hashGeneratorMaxPoolSize;

    @Value("${executor.hash-generator.queue-capacity:100}")
    private int hashGeneratorQueueCapacity;

    @Value("${executor.hash-generator.prefix:HashGenerator-}")
    private String hashGeneratorPrefix;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorCorePoolSize);
        executor.setMaxPoolSize(hashGeneratorMaxPoolSize);
        executor.setQueueCapacity(hashGeneratorQueueCapacity);
        executor.setThreadNamePrefix(hashGeneratorPrefix);
        executor.initialize();
        return executor;
    }
}
