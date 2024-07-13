package faang.school.urlshortenerservice.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${hash.thread-pool.pool}")
    private int nThreads;

    @Bean
    public Executor hashThreadPool() {
        return Executors.newFixedThreadPool(nThreads);
    }
}
