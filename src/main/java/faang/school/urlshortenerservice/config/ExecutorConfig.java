package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ExecutorConfig {

    @Value("${app.async.core-pool-size}")
    private int corePoolSize;
    @Value("${app.async.max-pool-size}")
    private int maxPoolSize;
    @Value("${app.async.queue-capacity}")
    private int queueCapacity;
    @Value("${app.async.thread_name_prefix}")
    private String hashGenerator;

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(hashGenerator);
        executor.initialize();
        return executor;
    }

}