package faang.school.urlshortenerservice.config.streams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorServices extends TreadPool {

    @Value("${spring.task.execution2.pool.core-size}")
    private int coreSize;
    @Value("${spring.task.execution2.pool.max-size}")
    private int maxSize;
    @Value("${spring.task.execution2.pool.keep-alive}")
    private int keepAlive;
    @Value("${spring.task.execution2.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.task.execution2.pool.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "executorService")
    public ThreadPoolExecutor asyncExecutor() {
        return getThreadPoolExecutor(coreSize, maxSize, queueCapacity, keepAlive, threadNamePrefix);
    }
}
