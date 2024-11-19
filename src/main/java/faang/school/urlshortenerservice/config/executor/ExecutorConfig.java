package faang.school.urlshortenerservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class ExecutorConfig {

    private final HashGeneratorExecutorParams hashGeneratorExecutorParams;
    private final HashCacheExecutorParams hashCacheExecutorParams;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorExecutorParams.getCorePoolSize());
        executor.setMaxPoolSize(hashGeneratorExecutorParams.getMaxPoolSize());
        executor.setQueueCapacity(hashGeneratorExecutorParams.getQueueCapacity());
        executor.setThreadNamePrefix(hashGeneratorExecutorParams.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                hashCacheExecutorParams.getCorePoolSize(),
                hashCacheExecutorParams.getMaxPoolSize(),
                hashCacheExecutorParams.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
    }
}
