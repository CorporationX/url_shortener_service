package faang.school.urlshortenerservice.config.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Value("${data.url.cache.pool-size}")
    private int poolSize;

    @Bean(name = "hashExecutor")
    public ExecutorService hashExecutors() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
