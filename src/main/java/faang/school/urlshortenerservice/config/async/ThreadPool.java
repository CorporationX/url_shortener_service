package faang.school.urlshortenerservice.config.async;

import faang.school.urlshortenerservice.properties.HashCacheFillExecutorProperties;
import faang.school.urlshortenerservice.properties.HashGeneratorExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPool {

    private final HashGeneratorExecutorProperties hashGenerationProp;
    private final HashCacheFillExecutorProperties hashCacheFillingProp;

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGenerationProp.getCorePoolSize());
        executor.setMaxPoolSize(hashGenerationProp.getMaxPoolSize());
        executor.setQueueCapacity(hashGenerationProp.getQueueCapacity());
        executor.setThreadNamePrefix("async-hash-gen-exec-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor hashCacheFillExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCacheFillingProp.getCorePoolSize());
        executor.setMaxPoolSize(hashCacheFillingProp.getMaxPoolSize());
        executor.setQueueCapacity(hashCacheFillingProp.getQueueCapacity());
        executor.setThreadNamePrefix("async-hash-fill-exec-");
        executor.initialize();
        return executor;
    }
}
