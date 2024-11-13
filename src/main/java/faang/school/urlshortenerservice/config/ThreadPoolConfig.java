package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import properties.ThreadProperties;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadProperties tp = new ThreadProperties();

    @Bean(name = "HashGeneratorPool")
    public Executor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(tp.getGenerator().getCore());
        executor.setMaxPoolSize(tp.getGenerator().getMax());
        executor.setQueueCapacity(tp.getGenerator().getQueue());
        return executor;
    }

    @Bean(name = "HashCachePool")
    public ThreadPoolTaskExecutor hashCachePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(tp.getCache().getCore());
        executor.setMaxPoolSize(tp.getCache().getMax());
        executor.setQueueCapacity(tp.getCache().getQueue());
        return executor;
    }
}
