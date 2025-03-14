package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashUtilConfiguration {
    @Value("${hash.generator.thead-pool.core-pool-size}")
    private int generatorCorePoolSize;

    @Value("${hash.generator.thead-pool.max-pool-size}")
    private int generatorMaxPoolSize;

    @Value("${hash.generator.thead-pool.queue-capacity}")
    private int generatorQueueCapacity;

    @Value("${hash.generator.thead-pool.keep-alive-seconds}")
    private int generatorKeepAliveSeconds;


    @Value("${hash.cache.thead-pool.core-pool-size}")
    private int cacheCorePoolSize;

    @Value("${hash.cache.thead-pool.max-pool-size}")
    private int cacheMaxPoolSize;

    @Value("${hash.cache.thead-pool.queue-capacity}")
    private int cacheQueueCapacity;

    @Value("${hash.cache.thead-pool.keep-alive-seconds}")
    private int cacheKeepAliveSeconds;

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generatorCorePoolSize);
        executor.setMaxPoolSize(generatorMaxPoolSize);
        executor.setQueueCapacity(generatorQueueCapacity);
        executor.setKeepAliveSeconds(generatorKeepAliveSeconds);
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor hashCacheThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cacheCorePoolSize);
        executor.setMaxPoolSize(cacheMaxPoolSize);
        executor.setQueueCapacity(cacheQueueCapacity);
        executor.setKeepAliveSeconds(cacheKeepAliveSeconds);
        executor.initialize();
        return executor;
    }
}
