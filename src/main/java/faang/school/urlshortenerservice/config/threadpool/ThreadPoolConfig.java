package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${thread-pool.pool-size.hash-generator.max}")
    private int hashGeneratorMaxPoolSize;

    @Value("${thread-pool.pool-size.hash-generator.core}")
    private int hashGeneratorCorePoolSize;

    @Value("${thread-pool.pool-size.scheduler.core}")
    private int schedulerPoolSize;

    @Bean(name = "HashGeneratorThreadPool")
    public ThreadPoolTaskExecutor getHashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGeneratorCorePoolSize);
        executor.setMaxPoolSize(hashGeneratorMaxPoolSize);
        executor.setThreadNamePrefix("HashThreadPool - ");
        executor.initialize();
        return executor;
    }

    @Bean(name = "SchedulerThreadPool")
    public ThreadPoolTaskExecutor getSchedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(schedulerPoolSize);
        executor.setThreadNamePrefix("Scheduler - ");
        executor.initialize();
        return executor;
    }
}
