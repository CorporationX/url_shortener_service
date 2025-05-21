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

    private final HashCacheProperties properties;

    @Bean(name = "hashCacheExecutor")
    public ExecutorService hashCacheExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                properties.getExecutorConfig().getCorePoolSize(),
                properties.getExecutorConfig().getMaxPoolSize(),
                properties.getExecutorConfig().getKeepAliveTime(),
                java.util.concurrent.TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(properties.getExecutorConfig().getQueueCapacity())
        );
        executor.setThreadFactory(Executors.defaultThreadFactory());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
