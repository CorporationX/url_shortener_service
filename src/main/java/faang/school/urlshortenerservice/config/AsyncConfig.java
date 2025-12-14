package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Value("${hash.generator.thread-pool.core-size:5}")
    private int corePoolSize;

    @Value("${hash.generator.thread-pool.max-size:10}")
    private int maxPoolSize;

    @Value("${hash.generator.thread-pool.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        log.info("Creating hashGeneratorExecutor with corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                corePoolSize, maxPoolSize, queueCapacity);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-generator-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        return executor;
    }
}