package faang.school.urlshortenerservice.config.context;

import faang.school.urlshortenerservice.properties.HashProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class HashCacheConfig {

    private final HashProperties hashProperties;

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashProperties.getCache().getExecutor().getPoolSize());
        executor.setMaxPoolSize(hashProperties.getCache().getExecutor().getPoolSize());
        executor.setQueueCapacity(hashProperties.getCache().getExecutor().getQueueSize());
        executor.setThreadNamePrefix("HashCache-");
        executor.initialize();
        return executor;
    }
}
