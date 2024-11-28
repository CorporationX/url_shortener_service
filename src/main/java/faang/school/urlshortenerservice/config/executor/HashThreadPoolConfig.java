package faang.school.urlshortenerservice.config.executor;

import faang.school.urlshortenerservice.config.executor.rejection.CustomCallerRunsPolicy;
import faang.school.urlshortenerservice.config.properties.hash.HashProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class HashThreadPoolConfig {

    private final HashProperties hashProperties;

    @Bean(name = "hashGeneratorExecutor")
    @Primary
    public TaskExecutor taskExecutor() {
        int initialPoolSize = hashProperties.getThreadPool().getInitialPoolSize();
        int maxPoolSize = hashProperties.getThreadPool().getMaxPoolSize();
        int queueCapacity = hashProperties.getQueue().getCapacity();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(initialPoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("HashGeneratorThread-");
        taskExecutor.initialize();
        taskExecutor.setRejectedExecutionHandler(new CustomCallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "hashCacheGeneratorExecutor")
    public ExecutorService hashCacheExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
