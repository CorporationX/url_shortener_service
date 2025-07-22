package faang.school.urlshortenerservice.config.executors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties({HashGeneratorPoolProperties.class, HashCachePoolProperties.class})
public class ExecutorsConfig {

    @Bean(name = "hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor(HashGeneratorPoolProperties p) {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(p.poolSize());
        ex.setMaxPoolSize(p.poolSize());
        ex.setQueueCapacity(p.queueCapacity());
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(p.awaitSeconds());
        ex.setThreadNamePrefix("hash-gen-");
        ex.initialize();
        return ex;
    }

    @Bean(name = "hashCacheExecutor")
    public ThreadPoolTaskExecutor hashCacheExecutor(HashCachePoolProperties p) {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(p.poolSize());
        ex.setMaxPoolSize(p.poolSize());
        ex.setQueueCapacity(p.queueCapacity());
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(p.awaitSeconds());
        ex.setThreadNamePrefix("hash-cache-");
        ex.initialize();
        return ex;
    }
}
