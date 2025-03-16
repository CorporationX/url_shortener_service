package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolHashing {
    @Bean(name = "hashThreadPool")
    public ExecutorService executorService(@Value("${hash.generator.thread-pool-size}") int threadPoolSize) {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
