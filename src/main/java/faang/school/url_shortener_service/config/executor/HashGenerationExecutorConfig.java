package faang.school.url_shortener_service.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
public class HashGenerationExecutorConfig {

    @Value("${executor.hash-generation.core-pool-size}")
    private int corePoolSize;
    @Value("${executor.hash-generation.queue-capacity}")
    private int queueCapacity;
    @Value("${executor.hash-generation.threadNamePrefix}")
    private String threadNamePrefix;
    @Value("${executor.hash-generation.allowCoreThreadTimeOut}")
    private boolean allowCoreThreadTimeOut;
    @Value("${executor.hash-generation.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Bean(name = "hashGenerationExecutor")
    public Executor hashGenerationExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(corePoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}