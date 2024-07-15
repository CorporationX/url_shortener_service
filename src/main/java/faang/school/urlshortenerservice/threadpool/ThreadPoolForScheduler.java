package faang.school.urlshortenerservice.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForScheduler {

    @Value("${threadPool.scheduler}")
    private int nThread;

    @Bean
    public ExecutorService scheduler() {
        return Executors.newFixedThreadPool(nThread);
    }
}
