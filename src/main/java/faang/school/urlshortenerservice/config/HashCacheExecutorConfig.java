package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class HashCacheExecutorConfig {

    @Value("${hash.cache.pool-name:HashCache-}")
    private String poolName;

    @Value("${hash.cache.pool-size:4}")
    private int poolSize;

    @Bean(name = "hashCacheExecutor")
    public Executor hashCacheExecutor() {
        return Executors.newFixedThreadPool(poolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(poolName + thread.getId());
            return thread;
        });
    }

}
