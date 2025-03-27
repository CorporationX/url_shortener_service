package faang.school.urlshortenerservice.config;

import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class GeneratorConfig {
    @Value("${hash.core-Pool-Size}")
    private int threadPoolSize;
    @Value("${hash.queue-Capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorThreadPool")
    public ThreadPoolTaskExecutor customThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}
