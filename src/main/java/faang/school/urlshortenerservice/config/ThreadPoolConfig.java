package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "schedulers.config")
@Getter
@Setter
public class ThreadPoolConfig {
    private int corePoolSize;
    private int maximumPoolSize;
    private int queueCapacity;

    @Value("${schedulers.config.hashesGenerator.threadNamePrefix}")
    private String publishThreadNamePrefix;

    @Bean(name = "HashesGeneratorThreadPool")
    public ThreadPoolTaskExecutor getHashesGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(publishThreadNamePrefix);
        executor.initialize();
        return executor;
    }
}
