package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {

    @Value("${hash.executor.core-size:10}")
    private int hashCorePoolSize;

    @Value("${hash.executor.max-size:20}")
    private int hashMaxPoolSize;

    @Value("${hash.executor.queue-capacity:100}")
    private int hashQueueCapacity;

    @Value("${hash.executor.prefix:HashGenerator-}")
    private String hashPrefix;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCorePoolSize);
        executor.setMaxPoolSize(hashMaxPoolSize);
        executor.setQueueCapacity(hashQueueCapacity);
        executor.setThreadNamePrefix(hashPrefix);
        executor.initialize();
        return executor;
    }
}
