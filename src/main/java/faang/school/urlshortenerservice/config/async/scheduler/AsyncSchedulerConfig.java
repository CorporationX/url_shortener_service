package faang.school.urlshortenerservice.config.async.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncSchedulerConfig {
    @Value("${async.scheduler.corePoolSize}")
    private int corePoolSize;

    @Value("${async.scheduler.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${async.scheduler.queueCapacity}")
    private int queueCapacity;

    @Value("${async.scheduler.keepAliveTime}")
    private int keepAliveTime;

    @Bean
    public ExecutorService schedulerExecutorService() {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueCapacity));
    }
}
