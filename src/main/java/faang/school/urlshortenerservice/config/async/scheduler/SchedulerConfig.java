package faang.school.urlshortenerservice.config.async.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Value("${url.cleaning.thread-pool:1}")
    private int urlCleaningThreadPoolSize;

    @Bean
    public ExecutorService urlCleaningExecutor() {
        return Executors.newFixedThreadPool(urlCleaningThreadPoolSize);
    }
}
