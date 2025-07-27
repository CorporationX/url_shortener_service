package faang.school.urlshortenerservice.config.hash;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class HashConfig {
    HashProperties hashProperties;

    @Bean
    public Base62 base62() {
        return Base62.createInstance();
    }

    @Bean(name = "hashGeneratorPool")
    public TaskExecutor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashProperties.getCorePoolSize());
        executor.setMaxPoolSize(hashProperties.getMaxPoolSize());
        executor.setQueueCapacity(hashProperties.getQueueCapacity());
        executor.setThreadNamePrefix(hashProperties.getPrefix());
        executor.initialize();
        return executor;
    }
}