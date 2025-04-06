package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${data.cache.pool-size}")
    private int poolSize;

    @Bean(name = "hashExecutor")
    public ExecutorService hashExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
