package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class HashAsyncConfig {

    @Value("${hash.generator.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${hash.generator.thread-pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${hash.generator.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Value("${hash.generator.thread-pool.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Bean(name = "hashGeneratorThreadPool")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}