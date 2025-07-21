package faang.school.urlshortenerservice.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import faang.school.urlshortenerservice.config.MainConfig;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class CustomTaskExecutor extends ThreadPoolTaskExecutor {
    private final MainConfig mainConfig;

    public ThreadPoolTaskExecutor customTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(mainConfig.getCorePoolSize());          // Initial number of threads
        executor.setMaxPoolSize(mainConfig.getMaxPoolSize());          // Maximum number of threads
        executor.setQueueCapacity(mainConfig.getQueueCapacity());      // Queue capacity for holding tasks
        executor.setThreadNamePrefix(mainConfig.getThreadNamePrefix()); // Thread name prefix
        executor.initialize();
        return executor;
    }
}
