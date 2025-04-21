package faang.school.urlshortenerservice.config.context;

import faang.school.urlshortenerservice.property.UrlShortenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final UrlShortenerProperties properties;

    @Bean
    public ExecutorService executorService() {
        UrlShortenerProperties.ExecutorService executorService = properties.getExecutorService();

        return new ThreadPoolExecutor(
                executorService.getCorePoolSize(),
                executorService.getMaxPoolSize(),
                executorService.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(executorService.getQueueCapacity()));
    }
}
