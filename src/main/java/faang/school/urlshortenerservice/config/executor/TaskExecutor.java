package faang.school.urlshortenerservice.config.executor;

import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class TaskExecutor {
    private final UrlShortenerConfig config;

    @Bean(name = "urlShortenerExecutor")
    public ThreadPoolTaskExecutor urlShortenerExecutor(
    ) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(config.getPoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(config.getPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(config.getQueueSize());
        threadPoolTaskExecutor.setThreadNamePrefix(config.getExecutorPrefix());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
