package faang.school.urlshortenerservice.config.async;

import faang.school.urlshortenerservice.properties.HashesCreatingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class HashesCreatingAsyncConfig {

    private final HashesCreatingProperties properties;

    @Bean(name = "hashesCreator")
    public ThreadPoolTaskExecutor createExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.size());
        executor.setThreadNamePrefix(properties.threadPrefix());
        executor.setWaitForTasksToCompleteOnShutdown(properties.isWaitShutdown());
        executor.setAwaitTerminationSeconds(properties.shutdownTimeoutSetting());
        executor.initialize();
        return executor;
    }
}
