package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfig {

    @Value("${hash-generator.executor.core-size}")
    private int coreSize;

    @Value("${hash-generator.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${hash-generator.executor.queue-capacity}")
    private int queueCapacity;

    @Value("${hash-generator.executor.ttl-in-seconds}")
    private long ttlInSeconds;

    @Bean(name = "hashGeneratorExecutor")
    public ExecutorService hashGeneratorExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize,
                maxPoolSize,
                ttlInSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}