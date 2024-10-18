package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor(@Value("${data.hash.generator.executor.pool_size}") int poolSize,
                                          @Value("${data.hash.generator.executor.max_pool_size}") int maxPoolSize,
                                          @Value("${data.hash.generator.executor.queue_capacity}") int queueCapacity,
                                          @Value("${data.hash.generator.executor.prefix}") String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();
        return executor;
    }
}
