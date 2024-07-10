package faang.school.urlshortenerservice.threadpool;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolForGenerateBatch {

    @Value("${threadPool.hashGenerator}")
    private int nThreads;

    @Bean
    public ExecutorService generateBatchPool() {
        return Executors.newFixedThreadPool(nThreads);
    }
}