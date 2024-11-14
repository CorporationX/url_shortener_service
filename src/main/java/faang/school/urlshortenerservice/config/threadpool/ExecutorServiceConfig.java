package faang.school.urlshortenerservice.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ExecutorServiceConfig {

    private final ExecutorProperties executorProperties;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                executorProperties.getCorePoolSize(),
                executorProperties.getMaxPoolSize(),
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(executorProperties.getQueueCapacity()),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
