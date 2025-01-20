package faang.school.urlshortenerservice.config.thread;

import faang.school.urlshortenerservice.properties.thread.HashCacheExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class HashCacheExecutorConfig {

    private final HashCacheExecutorProperties hashCacheExecutorProperties;

    @Bean
    public ExecutorService hashCacheExecutorService() {
        return new ThreadPoolExecutor(
                hashCacheExecutorProperties.getCorePoolSize(),
                hashCacheExecutorProperties.getMaxPoolSize(),
                hashCacheExecutorProperties.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(hashCacheExecutorProperties.getQueueCapacity())
        );
    }
}
