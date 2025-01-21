package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CleanUrlSchedulePoolConfig {
    @Bean
    public ExecutorService cleanUrlSchedulePool() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}