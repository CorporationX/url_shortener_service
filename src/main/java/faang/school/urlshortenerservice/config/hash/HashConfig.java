package faang.school.urlshortenerservice.config.hash;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class HashConfig {
    @Value("${hash.core_pool_size}")
    private int corePoolSize;
    @Value("${hash.max_pool_size}")
    private int maxPoolSize;
    @Value("${hash.queue_capacity}")
    private int queueCapacity;
    @Value("${hash.prefix}")
    private String prefix;

    @Bean
    public Base62 base62() {
        return Base62.createInstance();
    }

    @Bean(name = "hashGeneratorPool")
    public TaskExecutor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();
        return executor;
    }
}