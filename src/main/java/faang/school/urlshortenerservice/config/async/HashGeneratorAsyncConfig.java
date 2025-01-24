package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class HashGeneratorAsyncConfig {
    @Value("${app.async-config.core-pool-size}")
    private int corePoolSize;

    @Value("${app.async-config.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.async-config.queue-capacity}")
    private int queueCapacity;

    @Bean
    public Executor generateHashPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();

        return executor;
    }
}
