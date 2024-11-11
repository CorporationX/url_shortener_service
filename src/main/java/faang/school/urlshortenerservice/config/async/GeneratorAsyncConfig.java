package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class GeneratorAsyncConfig {

    @Value("${hash.generator.async.core-pool-size}")
    private int corePoolSize;
    @Value("${hash.generator.async.max-pool-size}")
    private int maxPoolSize;
    @Value("${hash.generator.async.queue-capacity}")
    private int queueCapacity;
    @Value("${hash.generator.async.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor generatorThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }
}
