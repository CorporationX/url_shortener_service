package faang.school.urlshortenerservice.config.executor;

import faang.school.urlshortenerservice.config.propertis.hash.ThreadProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Getter
@Setter
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ExecutorServiceConfig {

    private final ThreadProperties properties;

    @Bean(name = "hashGeneratorExecutor")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix(properties.getThreadName());
        executor.initialize();
        return executor;
    }

}