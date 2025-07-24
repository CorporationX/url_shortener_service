package faang.school.urlshortenerservice.config.hash;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CacheConfig {
    @Bean
    public ExecutorService hashCachePool(CacheProperties cacheProperties) {
        return Executors.newFixedThreadPool(cacheProperties.poolSize());
    }
}
