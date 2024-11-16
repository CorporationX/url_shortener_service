package faang.school.urlshortenerservice.util.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class TaskExecutorConfig {

    private final TaskExecutorParams params;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(params.getCorePoolSize());
        executor.setMaxPoolSize(params.getMaxPoolSize());
        executor.setQueueCapacity(params.getQueueCapacity());
        executor.setThreadNamePrefix(params.getThreadNamePrefix());
        executor.initialize();

        return executor;
    }
}
