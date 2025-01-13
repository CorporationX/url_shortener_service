package faang.school.urlshortenerservice.config.asyncExecutor;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncExecutor {

    @Value("${async.CorePoolSize}")
    private int corePoolSize;
    @Value("${async.MaxPoolSize}")
    private int maxPoolSize;
    @Value("${async.queueCapacity}")
    private int queueCapacity;
    @Value("${async.threadPrefixName}")
    private String threadPrefixName;

    private ThreadPoolTaskExecutor asyncExecutor;

    @Bean
    public TaskExecutor taskExecutor() {
        asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(corePoolSize);
        asyncExecutor.setMaxPoolSize(maxPoolSize);
        asyncExecutor.setQueueCapacity(queueCapacity);
        asyncExecutor.setThreadNamePrefix(threadPrefixName);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (asyncExecutor != null) {
            asyncExecutor.shutdown();
        }
    }
}
