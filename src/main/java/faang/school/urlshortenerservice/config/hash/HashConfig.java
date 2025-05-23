package faang.school.urlshortenerservice.config.hash;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Getter
public class HashConfig {
    @Value("${hash.generator.batch-size}")
    private int numberOfElements;
    @Value("${hash.repository.fetch-limit:1000}")
    private long fetchLimit;
    @Value("${hash.repository.partition-size:100}")
    private int partitionSize;

    @Bean("hashGeneratorThreadPool")
    public ExecutorService hashGeneratorThreadPool(
            @Value("${hash.generator.executor.pool_size:4}") int poolSize,
            @Value("${hash.generator.executor.queue_capacity:10}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("hash-generator-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}