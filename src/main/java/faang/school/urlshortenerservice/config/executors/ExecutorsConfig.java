package faang.school.urlshortenerservice.config.executors;

import faang.school.urlshortenerservice.config.properties.HashCachePoolProperties;
import faang.school.urlshortenerservice.config.properties.HashGeneratorPoolProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties({HashGeneratorPoolProperties.class, HashCachePoolProperties.class})
public class ExecutorsConfig {

    @Bean(name = "hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor(HashGeneratorPoolProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.poolSize());
        executor.setMaxPoolSize(properties.poolSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(properties.awaitSeconds());
        executor.setThreadNamePrefix(properties.threadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashCacheExecutor")
    public ThreadPoolTaskExecutor hashCacheExecutor(HashCachePoolProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.poolSize());
        executor.setMaxPoolSize(properties.poolSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(properties.awaitSeconds());
        executor.setThreadNamePrefix(properties.threadNamePrefix());
        executor.initialize();
        return executor;
    }
}
