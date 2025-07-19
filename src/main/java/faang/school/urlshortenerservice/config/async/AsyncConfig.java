package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final FillingMemoryCacheExecutorProperties fillingMemoryCacheExecutorProperties;
    @Bean(name = "fillingMemoryCacheExecutor")
    public Executor fillingMemoryCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(fillingMemoryCacheExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(fillingMemoryCacheExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(fillingMemoryCacheExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(fillingMemoryCacheExecutorProperties.getPrefix());
        executor.initialize();
        return executor;
    }
}
