package faang.school.urlshortenerservice.config.executor;

import faang.school.urlshortenerservice.config.properties.hash.HashCacheExecutorProperties;

import faang.school.urlshortenerservice.config.properties.hash.HashGeneratorExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class ExecutorServiceConfig {

    private final HashGeneratorExecutorProperties generatorExecProps;
    private final HashCacheExecutorProperties cacheExecProps;

    @Bean("hashGeneratorExecutorService")
    public Executor hashGeneratorExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generatorExecProps.corePoolSize());
        executor.setMaxPoolSize(generatorExecProps.maxPoolSize());
        executor.setQueueCapacity(generatorExecProps.queueCapacity());
        executor.initialize();
        return executor;
    }

    @Bean("hashCacheExecutor")
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cacheExecProps.corePoolSize());
        executor.setMaxPoolSize(cacheExecProps.maxPoolSize());
        executor.setQueueCapacity(cacheExecProps.queueCapacity());
        executor.initialize();
        return executor;
    }
}
