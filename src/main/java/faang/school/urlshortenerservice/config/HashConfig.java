package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HashConfig {
    @Value("${spring.hash-generator.pool-size}")
    private int generatorPoolSize;

    @Value("${spring.hash-generator.max-pool-size}")
    private int generatorMaxPoolSize;

    @Value("${spring.hash-generator.queue-capacity}")
    private int generatorQueueCapacity;

    @Value("${spring.hash-pull-up.pool-size}")
    private int pullUpHashPoolSize;

    @Value("${spring.hash-pull-up.max-pool-size}")
    private int pullUpHashMaxPoolSize;

    @Value("${spring.hash-pull-up.queue-capacity}")
    private int pullUpHashQueueCapacity;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Bean
    public ThreadPoolTaskExecutor generateBatchThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generatorPoolSize);
        executor.setMaxPoolSize(generatorMaxPoolSize);
        executor.setQueueCapacity(generatorQueueCapacity);
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor pullUpHashPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(pullUpHashPoolSize);
        executor.setMaxPoolSize(pullUpHashMaxPoolSize);
        executor.setQueueCapacity(pullUpHashQueueCapacity);
        return executor;
    }

    @Bean
    public String hostUrl() {
        return host + ":" + port + "/";
    }
}
