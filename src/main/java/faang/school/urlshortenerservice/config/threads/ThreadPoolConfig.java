package faang.school.urlshortenerservice.config.threads;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor(ThreadPoolProperties threadPoolProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.size());
        executor.setMaxPoolSize(threadPoolProperties.maxSize());
        executor.setThreadNamePrefix(threadPoolProperties.prefix());
        executor.initialize();
        return executor;
    }
}