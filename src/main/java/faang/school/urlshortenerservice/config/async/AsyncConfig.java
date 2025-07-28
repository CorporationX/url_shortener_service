package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@EnableAsync
@Configuration
public class AsyncConfig {

    private final HashCashExecutorProperties hashCashExecutorProperties;
    private final HashGeneratorExecutorProperties hashGeneratorExecutorProperties;

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(hashCashExecutorProperties.getThreadNamePrefix());
        executor.setCorePoolSize(hashCashExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(hashCashExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(hashCashExecutorProperties.getQueueCapacity());
        return executor;
    }

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(hashGeneratorExecutorProperties.getThreadNamePrefix());
        executor.setCorePoolSize(hashGeneratorExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(hashGeneratorExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(hashGeneratorExecutorProperties.getQueueCapacity());
        return executor;
    }

}
