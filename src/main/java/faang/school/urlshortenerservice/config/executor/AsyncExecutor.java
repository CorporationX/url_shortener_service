package faang.school.urlshortenerservice.config.executor;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncExecutor {
    @Value("${async.core-pool-size}")
    private int corePoolSize;

    @Value("${async.max-pool-size}")
    private int maxPoolSize;

    @Value("${async.queue-capacity}")
    private int queueCapacity;

    @Value("${async.thread-name-prefix}")
    private String threadNamePrefix;

    private ThreadPoolTaskExecutor asyncExecutor;


    @Bean("async")
    public ThreadPoolTaskExecutor taskExecutor() {
        asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(corePoolSize);
        asyncExecutor.setMaxPoolSize(maxPoolSize);
        asyncExecutor.setQueueCapacity(queueCapacity);
        asyncExecutor.setThreadNamePrefix(threadNamePrefix);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @PreDestroy
    public void shutDownAsyncExecutor() {
        if (asyncExecutor != null) {
            asyncExecutor.shutdown();
        }
    }
}
