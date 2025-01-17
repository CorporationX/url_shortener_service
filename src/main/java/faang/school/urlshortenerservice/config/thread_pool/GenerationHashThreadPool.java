package faang.school.urlshortenerservice.config.thread_pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class GenerationHashThreadPool {

    @Value("${app.hash.thread-pool.core-size}")
    private int coreSize;
    @Value("${app.hash.thread-pool.max-size}")
    private int maxSize;
    @Value("${app.hash.thread-pool.queue-capacity}")
    private int queueCapacity;
    @Value("${app.hash.thread-pool.keep-alive}")
    private int keepAlive;

    @Bean(name = "generateHashExecutor")
    public ThreadPoolExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAlive);
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}