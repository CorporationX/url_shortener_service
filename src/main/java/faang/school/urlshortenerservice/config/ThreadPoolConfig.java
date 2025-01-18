package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final HashServiceThreadPoolProperties hashProperties;
    private final LocalCacheThreadPoolProperties cacheProperties;

    @Bean(name = "hashServiceExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(hashProperties.corePoolSize());
        taskExecutor.setMaxPoolSize(hashProperties.maxPoolSize());
        taskExecutor.setQueueCapacity(hashProperties.queueCapacity());
        taskExecutor.setThreadNamePrefix(hashProperties.namePrefix());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean(name = "localCacheExecutor")
    public ThreadPoolTaskExecutor localCacheExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(cacheProperties.corePoolSize());
        taskExecutor.setMaxPoolSize(cacheProperties.maxPoolSize());
        taskExecutor.setThreadNamePrefix(cacheProperties.namePrefix());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
