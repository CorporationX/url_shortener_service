package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${spring.task.executor.pool.core-size}")
    private int corePoolSize;

    @Value("${spring.task.executor.pool.max-size}")
    private int maxPoolSize;

    @Value("${spring.task.executor.pool.keep-alive-time}")
    private long keepAliveTime;

    @Value("${spring.task.executor.pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
