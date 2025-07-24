package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfigHashCache {

    @Value("${executor.cache.max_thread_pool}")
    private int maxThreadPool;

    @Value("${executor.cache.core}")
    private int core;

    @Bean(name = "taskExecutorHashCache")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(maxThreadPool);
        executor.initialize();
        return executor;
    }
}
