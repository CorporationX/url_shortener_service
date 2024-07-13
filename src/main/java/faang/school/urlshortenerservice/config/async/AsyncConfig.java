package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${custom.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${custom.thread-pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${custom.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGeneratorThreadPool")
    public ThreadPoolTaskExecutor hashGeneratorThreadPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
