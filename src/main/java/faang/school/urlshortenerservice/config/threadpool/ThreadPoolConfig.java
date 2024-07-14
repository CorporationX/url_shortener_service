package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${thread-pool.pool-size.max}")
    private int maxPoolSize;

    @Value("${thread-pool.pool-size.core}")
    private int corePoolSize;

    @Bean(name = "HashGeneratorThreadPool")
    public ThreadPoolTaskExecutor getHashGeneratorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix("HashThreadPool - ");
        executor.initialize();
        return executor;
    }
}
