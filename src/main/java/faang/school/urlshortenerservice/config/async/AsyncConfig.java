package faang.school.urlshortenerservice.config.async;

import faang.school.urlshortenerservice.properties.HashesCreatingProperties;
import faang.school.urlshortenerservice.properties.PoolProperties;
import faang.school.urlshortenerservice.properties.QueueRefillingProperties;
import faang.school.urlshortenerservice.properties.SchedulerCustomProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final QueueRefillingProperties queueRefillingProperties;
    private final SchedulerCustomProperties schedulerCustomProperties;
    private final HashesCreatingProperties hashesCreatingProperties;

    @Primary
    @Bean(name = "queueRefillingPool")
    public ThreadPoolTaskExecutor createQueueRefillingPool() {
        return getExecutor(queueRefillingProperties);
    }

    @Bean(name = "schedulerCustom")
    public ThreadPoolTaskExecutor createSchedulerCustomPool() {
        return getExecutor(schedulerCustomProperties);
    }

    @Bean(name = "hashesCreator")
    public ThreadPoolTaskExecutor createHashesCreatorPool() {
        return getExecutor(hashesCreatingProperties);
    }

    private ThreadPoolTaskExecutor getExecutor(PoolProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.size());
        executor.setThreadNamePrefix(properties.threadPrefix());
        executor.setWaitForTasksToCompleteOnShutdown(properties.isWaitShutdown());
        executor.setAwaitTerminationSeconds(properties.shutdownTimeoutSetting());
        executor.initialize();
        return executor;
    }
}
