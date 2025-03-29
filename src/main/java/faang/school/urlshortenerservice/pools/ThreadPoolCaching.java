package faang.school.urlshortenerservice.pools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolCaching {
    @Bean(name = "cacheThreadPool")
    public ExecutorService executorService(@Value("${hash.cache.thread-pool-size-caching}") int threadPoolSize) {
        return new ThreadPoolExecutor(
                threadPoolSize, threadPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
