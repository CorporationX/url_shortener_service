package faang.school.urlshortenerservice.config.streams;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public abstract class TreadPool {

    ThreadPoolExecutor getThreadPoolExecutor(int coreSize, int maxSize, int queueCapacity, int keepAlive, String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}
