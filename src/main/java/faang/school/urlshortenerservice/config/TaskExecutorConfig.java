package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

    @Value("${spring.executor.pool-size:5}")
    private int poolSize;

    @Bean
    public ThreadPoolTaskExecutor cacheLoaderPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.initialize();
        return executor;
    }
}
