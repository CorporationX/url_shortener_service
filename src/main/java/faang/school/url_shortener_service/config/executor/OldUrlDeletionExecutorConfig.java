package faang.school.url_shortener_service.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class OldUrlDeletionExecutorConfig {

    @Value("${executor.oldUrlDeletionExecutor.core-pool-size}")
    private int corePoolSize;
    @Value("${executor.oldUrlDeletionExecutor.queue-capacity}")
    private int queueCapacity;
    @Value("${executor.oldUrlDeletionExecutor.threadNamePrefix}")
    private String threadNamePrefix;
    @Value("${executor.oldUrlDeletionExecutor.allowCoreThreadTimeOut}")
    private boolean allowCoreThreadTimeOut;
    @Value("${executor.oldUrlDeletionExecutor.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Bean(name = "oldUrlDeletionExecutor")
    public Executor oldUrlDeletionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}