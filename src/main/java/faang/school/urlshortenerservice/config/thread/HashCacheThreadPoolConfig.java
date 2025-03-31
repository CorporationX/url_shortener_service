package faang.school.urlshortenerservice.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class HashCacheThreadPoolConfig {

    @Value("${hash.local-cache.core-size}")
    private int corePoolSize;

    @Value("${hash.local-cache.max-size}")
    private int maxPoolSize;

    @Value("${hash.local-cache.keep-alive}")
    private int keepAliveSeconds;

    @Value("${hash.local-cache.queue-capacity}")
    private int queueCapacity;

    @Bean("hashCacheThreadPool")
    public Executor hashCacheThreadPool() {
        log.info("Создание пула потоков 'hashCacheThreadPool'...");
        log.info("Настройки пула: corePoolSize={}, maxPoolSize={}, keepAliveSeconds={}",
                corePoolSize, maxPoolSize, keepAliveSeconds);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashCacheThreadPool-");
        executor.initialize();

        log.info("Пул потоков 'HashCacheThreadPool' успешно создан.");
        return executor;
    }
}
