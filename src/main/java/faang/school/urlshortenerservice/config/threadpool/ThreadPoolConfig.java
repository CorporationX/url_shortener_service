package faang.school.urlshortenerservice.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties threadPoolProperties;

    @Bean(name = "hashExecutor")
    public ThreadPoolTaskExecutor hashExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCache().getExecutor().getPoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getCache().getExecutor().getPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getCache().getExecutor().getQueueSize());
        executor.setThreadNamePrefix("HashCache-");
        executor.initialize();
        return executor;
    }
}
