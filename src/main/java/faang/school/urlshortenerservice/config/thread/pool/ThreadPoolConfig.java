package faang.school.urlshortenerservice.config.thread.pool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolTaskExecutorProperties threadPoolTaskExecutorProperties;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(threadPoolTaskExecutorProperties.corePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolTaskExecutorProperties.maxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(threadPoolTaskExecutorProperties.queueCapacity());
        threadPoolTaskExecutor.setThreadNamePrefix(threadPoolTaskExecutorProperties.threadNamePrefix());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
