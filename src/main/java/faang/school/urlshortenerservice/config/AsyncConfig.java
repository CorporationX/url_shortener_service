package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final UrlShortenerProperties urlProperties;

    @Bean
    public TaskExecutor hashGeneratorThreadPool() {
        UrlShortenerProperties.ExecutorProperties properties = urlProperties.getHashGeneratorThreadPool();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix("Hash-Generator-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService hashCacheExecutorService() {
    UrlShortenerProperties.ExecutorProperties properties = urlProperties.getExecutorService();

        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }
}
