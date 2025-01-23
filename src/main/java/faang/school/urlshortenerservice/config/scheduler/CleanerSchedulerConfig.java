package faang.school.urlshortenerservice.config.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CleanerSchedulerConfig {
    @Value("${hash-cleaner.pool-size}")
    private int cleanerSchedulerPoolSize;

    @Bean
    public ExecutorService cleanerSchedulerThreadPool() {
        return Executors.newFixedThreadPool(cleanerSchedulerPoolSize);
    }
}
