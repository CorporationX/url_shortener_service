package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.properties.ThreadPoolProperties;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Setter
@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadPoolProperties properties;

    @Bean
    public ThreadPoolTaskExecutor threadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.initialize();
        return executor;
    }
}
