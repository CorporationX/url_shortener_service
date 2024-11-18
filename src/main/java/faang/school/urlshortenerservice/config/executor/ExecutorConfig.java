package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;

@Configuration
public class ExecutorConfig {

    @Value("${hash.generator.core-pool-size}")
    private int generationCorePoolSize;

    @Value("${hash.filling.core-pool-size}")
    private int fillingCorePoolSize;

    @Value("${hash.generator.max-pool-size}")
    private int generationMaxPoolSize;

    @Value("${hash.filling.max-pool-size}")
    private int fillingMaxPoolSize;

    @Value("${hash.generator.queue-capacity}")
    private int generationQueueCapacity;

    @Value("${hash.filling.queue-capacity}")
    private int fillingQueueCapacity;

    @Bean(name = "hashFillingExecutor")
    public ExecutorService hashFillingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(fillingCorePoolSize);
        executor.setMaxPoolSize(fillingMaxPoolSize);
        executor.setQueueCapacity(fillingQueueCapacity);
        executor.setThreadNamePrefix("HashFiller-");
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

    @Bean(name = "hashGenerationExecutor")
    public ExecutorService hashGenerationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generationCorePoolSize);
        executor.setMaxPoolSize(generationMaxPoolSize);
        executor.setQueueCapacity(generationQueueCapacity);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}
