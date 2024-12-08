package baum.urlshortenerservice.config.threads;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadPoolProperties properties;

    @Bean(name = "customThreadPool")
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(properties.getSize(),
                properties.getMaxSize(),
                properties.getTimeout(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
    }
}
