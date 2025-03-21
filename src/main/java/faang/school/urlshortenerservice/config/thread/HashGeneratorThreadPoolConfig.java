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
public class HashGeneratorThreadPoolConfig {

    @Value("${hash.hash-generator.core-size}")
    private int corePoolSize;

    @Value("${hash.hash-generator.max-size}")
    private int maxPoolSize;

    @Value("${hash.hash-generator.keep-alive}")
    private int keepAliveSeconds;
    @Value("${hash.hash-generator.queue-capacity}")
    private int queueCapacity;

    @Bean("hashGenerationThreadPool")
    public Executor hashGenerationThreadPool() {
        log.info("Создание пула потоков 'hashGenerationThreadPool'...");
        log.info("Настройки пула: corePoolSize={}, maxPoolSize={}, keepAliveSeconds={}",
                corePoolSize, maxPoolSize, keepAliveSeconds);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("HashGenerationThreadPool-");
        executor.initialize();

        log.info("Пул потоков 'hashGenerationThreadPool' успешно создан.");
        return executor;
    }
}
