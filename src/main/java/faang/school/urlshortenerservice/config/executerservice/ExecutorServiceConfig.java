package faang.school.urlshortenerservice.config.executerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.concurrent.*;

@Configuration
public class ExecutorServiceConfig {


    @Bean(name = "executorService")
    public ExecutorService executorService(@Value("${executorService.pool.size}") int poolSize,
                                           @Value("${executorService.queue.capacity}") int queueCapacity) {

        return new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueCapacity));
    }
}
