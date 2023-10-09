package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

@Configuration
public class AsyncConfiguration {

    @Value("${spring.async.hashGenerator.corePoolSize:2}")
    private int corePoolSize;
    @Value("${spring.async.hashGenerator.maxPoolSize:2}")
    private int maxPoolSize;
    @Value("${spring.async.hashGenerator.queueCapacity:100}")
    private int queueCapacity;
    @Value("${spring.async.hashGenerator.keepAliveTime:60}")
    private int keepAliveTime;
    @Value("${spring.async.hashGenerator.threadNamePrefix:HashGenerator-}")
    private String threadNamePrefix;
    @Value("${application.hashCache.capacity:1000}")
    private int cacheCapacity;

    @Bean
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);

        return executor;
    }

    @Bean
    Queue<String> hashes() {
        return new ArrayBlockingQueue<>(cacheCapacity);
    }
}
