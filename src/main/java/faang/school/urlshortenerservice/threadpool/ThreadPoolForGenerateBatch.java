package faang.school.urlshortenerservice.threadpool;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForGenerateBatch {

    @Value("${threadPool.hashGenerator}")
    private int nThread;

    @Bean
    public ExecutorService generateBatchPool() {
        return Executors.newFixedThreadPool(nThread);
    }
}