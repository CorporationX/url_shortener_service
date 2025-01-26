package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class HashGenerateAsyncConfig {

    @Value("${async-config.generate_hash.core_pool_size}")
    private int corePoolSize;

    @Value("${async-config.generate_hash.max_pool_size}")
    private int maxPoolSize;

    @Value("${async-config.generate_hash.queue_capacity}")
    private int queueCapacity;

    @Value("${async-config.generate_hash.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor generateHashPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

}
