package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class HashAsyncConfig {
    @Value("${generator.async.poolSize:5}")
    private int poolSize;

    @Value("${generator.async.maxPoolSize:10}")
    private int maxPoolSize;

    @Bean(name = "hashAsyncExecutor")
    public Executor hashAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix("HashAsync-");
        executor.initialize();

        return executor;
    }
}
