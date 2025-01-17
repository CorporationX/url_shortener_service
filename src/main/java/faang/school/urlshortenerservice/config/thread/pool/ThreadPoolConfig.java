package faang.school.urlshortenerservice.config.thread.pool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolTaskExecutorPropertiesConfig threadPoolTaskExecutorPropertiesConfig;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(threadPoolTaskExecutorPropertiesConfig.corePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolTaskExecutorPropertiesConfig.maxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(threadPoolTaskExecutorPropertiesConfig.queueCapacity());
        threadPoolTaskExecutor.setThreadNamePrefix(threadPoolTaskExecutorPropertiesConfig.threadNamePrefix());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
