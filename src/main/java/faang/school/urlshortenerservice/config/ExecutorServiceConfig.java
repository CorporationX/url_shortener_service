package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfig {
    @Value("${hash-generator.executor.core-size}")
    private int hashGeneratorCoreSize;
    @Value("${hash-generator.executor.max-pool-size}")
    private int hashGeneratorPoolMaxSize;
    @Value("${hash-generator.executor.queue-capacity}")
    private int hashGeneratorQueueSize;
    @Value("${hash-generator.executor.ttl-in-seconds}")
    private long hashGeneratorTtlInSeconds;

    @Bean(name = "hashGeneratorExecutor")
    public ExecutorService hashGeneratorExecutor() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(hashGeneratorQueueSize);
        return new ThreadPoolExecutor(
                hashGeneratorCoreSize,
                hashGeneratorPoolMaxSize,
                hashGeneratorTtlInSeconds,
                TimeUnit.SECONDS,
                workQueue
        );
    }
}
