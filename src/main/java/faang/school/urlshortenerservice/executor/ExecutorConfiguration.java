package faang.school.urlshortenerservice.executor;

import faang.school.urlshortenerservice.scheduler.shorter.ShorterCleanConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfiguration {

    private final HashGeneratorExecutorConfig hashGeneratorConfig;
    private final ShorterCleanConfig shorterCleanerConfig;

    @Primary
    @Bean(name = "hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorConfig.getCorePoolSize());
        executor.setMaxPoolSize(hashGeneratorConfig.getMaxPoolSize());
        executor.setThreadNamePrefix(hashGeneratorConfig.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "shorterCleanerExecutor")
    public ThreadPoolTaskExecutor shorterCleanerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(shorterCleanerConfig.getExecutorConfig().getCorePoolSize());
        executor.setMaxPoolSize(shorterCleanerConfig.getExecutorConfig().getMaxPoolSize());
        executor.setThreadNamePrefix(shorterCleanerConfig.getExecutorConfig().getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}