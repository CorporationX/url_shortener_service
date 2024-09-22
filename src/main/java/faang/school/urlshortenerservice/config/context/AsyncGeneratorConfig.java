package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncGeneratorConfig {
    @Value("${async.generator.poolSize}")
    private int poolSize;
    @Value("${async.generator.queueSize}")
    private int queueSize;

    @Bean(name = "asyncGenerator")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setQueueCapacity(queueSize);
        return executor;
    }
}
