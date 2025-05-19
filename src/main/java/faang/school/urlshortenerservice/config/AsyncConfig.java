package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Value("${app.hash.generator.thread.pool.core-pool-size}")
    private int hashGeneratorThreadCorePoolSize;

    @Value("${app.hash.generator.thread.pool.max-pool-size}")
    private int hashGeneratorThreadMaxPoolSize;

    @Value("${app.hash.generator.thread.pool.keep-alive-time}")
    private int hashGeneratorThreadKeepAliveTime;

    @Value("${app.hash.generator.thread.pool.queue-capacity}")
    private int hashGeneratorThreadQueueCapacity;

    @Bean(name = "hashGeneratorThreadPool")
    public ExecutorService hashGeneratorThreadPool() {
        return new ThreadPoolExecutor(
                hashGeneratorThreadCorePoolSize,
                hashGeneratorThreadMaxPoolSize,
                hashGeneratorThreadKeepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(hashGeneratorThreadQueueCapacity)
        );
    }
}
