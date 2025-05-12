package faang.school.urlshortenerservice.config.app;

import lombok.Data;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@ConfigurationProperties(prefix = "app.hash-cache")
@Data
public class HashCacheConfig {

    private int maxSize;
    private int refillThreshold;

    private ExecutorConfig executorConfig;

    @Data
    public static class ExecutorConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
    }

    @Bean(name = "hashCacheExecutor")
    public ExecutorService hashCacheExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                executorConfig.getCorePoolSize(),
                executorConfig.getMaxPoolSize(),
                0L,
                java.util.concurrent.TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(executorConfig.getQueueCapacity())
        );
        executor.setThreadFactory(Executors.defaultThreadFactory());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
