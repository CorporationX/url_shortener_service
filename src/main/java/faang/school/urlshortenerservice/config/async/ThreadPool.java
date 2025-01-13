package faang.school.urlshortenerservice.config.async;

import faang.school.urlshortenerservice.properties.HashCashFillExecutorProperties;
import faang.school.urlshortenerservice.properties.HashGeneratorExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPool {

    private final HashGeneratorExecutorProperties hashGenerationProp;
    private final HashCashFillExecutorProperties hashCashFillingProp;

    @Bean
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashGenerationProp.getCorePoolSize());
        executor.setMaxPoolSize(hashGenerationProp.getMaxPoolSize());
        executor.setQueueCapacity(hashGenerationProp.getQueueCapacity());
        executor.setThreadNamePrefix("async-hash-gen-exec-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor hashCacheFillExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashCashFillingProp.getCorePoolSize());
        executor.setMaxPoolSize(hashCashFillingProp.getMaxPoolSize());
        executor.setQueueCapacity(hashCashFillingProp.getQueueCapacity());
        executor.setThreadNamePrefix("async-hash-fill-exec-");
        executor.initialize();
        return executor;
    }
}
