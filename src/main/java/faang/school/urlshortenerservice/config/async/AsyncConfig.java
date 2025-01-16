package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final ThreadPoolProps threadPoolProps;

    public static final String HASH_GENERATOR_POOL = "hash-generator-pool";

    @Bean(name = HASH_GENERATOR_POOL)
    public ThreadPoolTaskExecutor hashGeneratorPool() {
        ThreadPoolProps.ThreadPool poolProps = threadPoolProps.getHashGeneratorPool();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolProps.getCorePoolSize());
        executor.setMaxPoolSize(poolProps.getMaxPoolSize());
        executor.setQueueCapacity(poolProps.getQueueCapacity());
        executor.setThreadNamePrefix(poolProps.getThreadPrefixName());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();

        return executor;
    }
}
