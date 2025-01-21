package faang.school.urlshortenerservice.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${thread-pool.default-amount}")
    private int threadPoolAmount;

    @Bean
    public ExecutorService executorServiceBean() {
        return Executors.newFixedThreadPool(threadPoolAmount);
    }
}
