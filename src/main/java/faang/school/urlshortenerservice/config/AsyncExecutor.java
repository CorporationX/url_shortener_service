package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncExecutor {

    private static final long MAX_WAIT_MILLIS = 300000;

    @Bean(name = "asyncHashGenerationExecutor")
    public ThreadPoolTaskExecutor expiredEventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setAwaitTerminationMillis(MAX_WAIT_MILLIS);
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("expiredEventTaskExecutor-");
        executor.initialize();
        return executor;
    }
}