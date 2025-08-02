package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    @Bean("hashGeneratorExecutor")
    public ExecutorService hashGeneratorExecutor(AsyncProperties asyncProperties) {
        return new ThreadPoolExecutor(
                asyncProperties.getThreadPoolSize(),
                asyncProperties.getThreadPoolSize(),
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(asyncProperties.getThreadPoolQueueCapacity()),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
