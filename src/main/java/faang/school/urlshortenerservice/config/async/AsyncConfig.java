package faang.school.urlshortenerservice.config.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableConfigurationProperties(ExecutorsProperties.class)
@RequiredArgsConstructor
public class AsyncConfig {

    private final ExecutorsProperties executorsProperties;

    public static final String FILL_CACHE_BEAN = "fillCacheExecutor";
    public static final String HASH_BATCH_BEAN = "hashBatchExecutor";

    @Bean(name = FILL_CACHE_BEAN)
    public Executor fillCacheExecutor() {
        return buildExecutor("fill-cache");
    }

    @Bean(name = HASH_BATCH_BEAN)
    public Executor hashBatchExecutor() {
        return buildExecutor("hash-batch");
    }

    private Executor buildExecutor(String key) {
        var executorProperties = executorsProperties.getPools().get(key);
        if (executorProperties == null) {
            throw new IllegalStateException("Executor props not found for key: " + key);
        }

        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(executorProperties.getCorePoolSize());
        ex.setMaxPoolSize(executorProperties.getMaxPoolSize());
        ex.setQueueCapacity(executorProperties.getQueueCapacity());
        ex.setThreadNamePrefix(executorProperties.getPrefix());

        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        ex.initialize();
        return ex;
    }
}
