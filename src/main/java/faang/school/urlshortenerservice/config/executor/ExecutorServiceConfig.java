package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorServiceConfig {
    @Value("${hash.generator.executor.core_pool_size}")
    private int corePoolSize;

    @Value("${hash.generator.executor.max_pool_size}")
    private int maxPoolSize;

    @Value("${hash.cache.capacity}")
    private int queueCapacity;

    @Value("${hash.generator.executor.name_prefix}")
    private String prefix;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();

        return executor;
    }
}
