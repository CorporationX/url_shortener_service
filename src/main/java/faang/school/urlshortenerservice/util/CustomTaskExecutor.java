package faang.school.urlshortenerservice.util;

import java.util.concurrent.ThreadPoolExecutor;

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
        executor.setCorePoolSize(mainConfig.getCorePoolSize());
        executor.setMaxPoolSize(mainConfig.getMaxPoolSize());
        executor.setQueueCapacity(mainConfig.getQueueCapacity());
        executor.setThreadNamePrefix(mainConfig.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
