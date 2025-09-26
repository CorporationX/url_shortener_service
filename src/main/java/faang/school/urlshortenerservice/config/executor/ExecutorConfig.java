package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {

    @Bean
    public Executor hashGeneratorExecutorService(
            @Value("${hash.generator.core-pool-size:2}") int corePoolSize,
            @Value("${hash.generator.max-pool-size:4}") int maxPoolSize,
            @Value("${hash.generator.queue-capacity:100}") int queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}