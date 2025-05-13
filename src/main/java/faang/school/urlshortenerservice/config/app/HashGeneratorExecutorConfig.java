package faang.school.urlshortenerservice.config.app;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class HashGeneratorExecutorConfig {

    private final HashGeneratorConfig hashGeneratorConfig;

    @Bean(name = "hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorConfig.getThreadPoolSize());
        executor.setMaxPoolSize(hashGeneratorConfig.getThreadPoolSize());
        executor.setQueueCapacity(hashGeneratorConfig.getThreadPoolQueueSize());
        executor.setThreadNamePrefix("HashGen-");
        executor.initialize();
        return executor;
    }
}
