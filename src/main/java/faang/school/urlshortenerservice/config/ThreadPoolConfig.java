package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import faang.school.urlshortenerservice.properties.ThreadProperties;

import java.util.concurrent.*;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadProperties tp;

    @Bean()
    public Executor hashGeneratorPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(tp.getGenerator().getCore());
        threadPoolTaskExecutor.setMaxPoolSize(tp.getGenerator().getMax());
        threadPoolTaskExecutor.setQueueCapacity(tp.getGenerator().getQueue());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean(name = "hashCachePool")
    public ExecutorService hashCachePool() {
        return new ThreadPoolExecutor(
                tp.getCache().getCore(),
                tp.getCache().getMax(),
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(tp.getCache().getQueue()),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
