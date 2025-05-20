package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.properties.ThreadPoolsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadPoolsProperties threadPoolsProperties;

    @Bean
    public ExecutorService getHashesPool() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService saveHashesPool() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService generateHashPool() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService saveToCachePool() {
        return Executors.newFixedThreadPool(threadPoolsProperties.getSizes().getSaveToCache());
    }
}
