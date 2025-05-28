package faang.school.urlshortenerservice.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties threadPoolProperties;

    @Bean(name = "hashExecutor")
    public ThreadPoolTaskExecutor hashExecutor() {
        var threadPoolPropertiesCache = threadPoolProperties.getCache();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolPropertiesCache.getExecutor().getPoolSize());
        executor.setMaxPoolSize(threadPoolPropertiesCache.getExecutor().getPoolSize());
        executor.setQueueCapacity(threadPoolPropertiesCache.getExecutor().getQueueSize());
        executor.setThreadNamePrefix("HashCache-");
        executor.initialize();
        return executor;
    }
}
