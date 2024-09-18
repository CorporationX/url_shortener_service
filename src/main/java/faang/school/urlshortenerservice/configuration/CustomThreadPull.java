package faang.school.urlshortenerservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CustomThreadPull {

    @Value("${hash.thread_pull_amount: 10}")
    private int threadPullAmount;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadPullAmount);
    }
}


