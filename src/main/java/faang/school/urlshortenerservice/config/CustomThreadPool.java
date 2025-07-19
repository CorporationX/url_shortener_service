package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class CustomThreadPool {

    @Value("${thread_pool.core_pool_size}")
    private int corePoolSize;

    @Value("${thread_pool.maximum_pool_size}")
    private int maximumPoolSize;

    @Value("${thread_pool.keep_alive_time}")
    private int keepAliveTime;

    @Bean
    public ExecutorService customPool() {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }


}
