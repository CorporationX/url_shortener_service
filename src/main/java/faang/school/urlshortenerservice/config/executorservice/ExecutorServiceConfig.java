package faang.school.urlshortenerservice.config.executorservice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorServiceConfig {
    private final ExecutorServiceProperties executorServiceProperties;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(executorServiceProperties.corePoolSize(),
                executorServiceProperties.maxPoolSize(),
                executorServiceProperties.keepAliveTime(),
                executorServiceProperties.timeUnit(),
                new LinkedBlockingQueue<>());
    }
}

