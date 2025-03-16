package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolHashing {
    @Value("${hash.generator.thread-pool-size}")
    private int threadPoolSize;

    public ExecutorService createExecutorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
