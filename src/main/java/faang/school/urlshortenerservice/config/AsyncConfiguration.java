package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.config.properties.ThreadPoolConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class AsyncConfiguration {
    private final ThreadPoolConfig threadPoolConfig;

    @Bean
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(threadPoolConfig.getSize());
    }
}