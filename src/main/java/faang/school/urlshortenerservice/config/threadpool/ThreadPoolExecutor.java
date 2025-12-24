package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolExecutor {

    @Value("${thread-pool.amount.hash-generator}")
    private int threadPoolAmountHashGenerator;

    @Bean("hashGeneratorExecutor")
    public ExecutorService hashGeneratorExecutor() {
        return Executors.newFixedThreadPool(threadPoolAmountHashGenerator);
    }

    @Bean("hashCacheExecutor")
    public ExecutorService hashCacheExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
