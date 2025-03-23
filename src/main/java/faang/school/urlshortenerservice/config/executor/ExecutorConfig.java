package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Configuration
public class ExecutorConfig {

    @Value("${executor.pool.size}")
    private int poolSize;

    @Value("${executor.max-pool.size}")
    private int maxPoolSize;

    @Value("${executor.queue.capacity}")
    private int queueCapacity;

    @Bean(name = "hashCacheExecutor", destroyMethod = "shutdown")
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                poolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(name = "hashGeneratorExecutor", destroyMethod = "shutdown")
    public ExecutorService generatorExecutorService() {
        return new ThreadPoolExecutor(
                poolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
