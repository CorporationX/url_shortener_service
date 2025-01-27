package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ExecutorServiceConfig {
    @Value("${tread-pool.all}")
    private int pool;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(pool);
    }
}