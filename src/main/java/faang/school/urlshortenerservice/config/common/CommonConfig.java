package faang.school.urlshortenerservice.config.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@EnableCaching
public class CommonConfig {
    @Value("${spring.thread-executor.corePoolSize}")
    int corePoolSize;
    @Value("${spring.thread-executor.maxPoolSize}")
    int maxPoolSize;
    @Value("${spring.thread-executor.waitForTasksToCompleteOnShutdown}")
    boolean isWaitForTasksToCompleteOnShutdown;
    @Value("${spring.thread-executor.threadNamePrefix}")
    String threadNamePrefix;

    @Bean(name = "threadPoolTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setWaitForTasksToCompleteOnShutdown(isWaitForTasksToCompleteOnShutdown);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
