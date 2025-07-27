package faang.school.urlshortenerservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfigJob {
    @Value("${moderation.executor.max_thread_pool_size}")
    private int maxThreadPoolSize;

    @Value("${moderation.executor.core_pool_size}")
    private int corePoolSize;

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxThreadPoolSize);
        executor.initialize();
        return executor;
    }

}
