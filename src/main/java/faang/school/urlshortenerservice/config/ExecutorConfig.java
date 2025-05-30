package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private final CacheProperties cacheProperties;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                cacheProperties.corePool(),
                cacheProperties.corePool(),
                cacheProperties.keepAlive(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(cacheProperties.capacity()),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
