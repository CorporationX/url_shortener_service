package faang.school.urlshortenerservice.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfigToHashGenerator {

    @Value("${executor.corePoolSize}")
    private int corePool;

    @Value("${executor.maxPoolSize}")
    private int maxPool;

    @Value("${executor.queueCapacity}")
    private int queueCapacity;

    @Bean
    public Executor customThreadPoolForHashGenerator() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("PoolForGenerateHash");
        executor.initialize();
        return executor;
    }

}
