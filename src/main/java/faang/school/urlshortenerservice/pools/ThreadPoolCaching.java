package faang.school.urlshortenerservice.pools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolCaching {
    @Bean(name = "cacheThreadPool")
    public ExecutorService executorService(@Value("${hash.cache.thread-pool-size-caching}") int threadPoolSize) {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
