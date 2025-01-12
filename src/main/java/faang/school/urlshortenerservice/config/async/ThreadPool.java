package faang.school.urlshortenerservice.config.async;

import faang.school.urlshortenerservice.properties.HashGeneratorExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPool {

    private final HashGeneratorExecutorProperties properties;

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix("async-exec-");
        executor.initialize();
        return executor;
    }
}
