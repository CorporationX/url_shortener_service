package faang.school.urlshortenerservice.config.asynconfig;

import faang.school.urlshortenerservice.config.hashconfig.HashConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    private final HashConfig hashConfig;

    public AsyncConfig(HashConfig hashConfig) {
        this.hashConfig = hashConfig;
    }

    @Bean(name = "hashGeneratorTaskExecutor")
    public Executor hashGenaratorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashConfig.getThreadPoolConfig().getCoreSize());
        executor.setMaxPoolSize(hashConfig.getThreadPoolConfig().getMaxSize());
        executor.setQueueCapacity(hashConfig.getThreadPoolConfig().getQueueSize());
        executor.setThreadNamePrefix("HashGenerator");
        executor.initialize();
        return executor;
    }
}