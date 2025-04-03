package faang.school.urlshortenerservice.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${hash-generator.thread-pool.size}")
    private int corePoolSize;

    @Value("${hash-generator.thread-pool.max-size}")
    private int maxPoolSize;

    @Value("${hash-generator.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}
