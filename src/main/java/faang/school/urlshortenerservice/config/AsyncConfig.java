package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Value("${hashgen.pool-name:HashGen-}")
    private String poolName;

    @Value("${hashgen.pool-size:4}")
    private int poolSize;

    @Bean(name = "hashGenExecutor")
    public Executor hashGenExecutor() {
        return Executors.newFixedThreadPool(poolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(poolName + thread.getId());
            return thread;
        });
    }
}
