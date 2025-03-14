package faang.school.urlshortenerservice.config.executor;

import faang.school.urlshortenerservice.properties.ThreadPoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService executorService(ThreadPoolProperties properties) {
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueCapacity()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
