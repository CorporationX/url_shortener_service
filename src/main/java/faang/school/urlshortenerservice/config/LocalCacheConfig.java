package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class LocalCacheConfig {
    private final UrlShortenerProperties properties;

    public LocalCacheConfig(UrlShortenerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ArrayBlockingQueue<Hash> localCache() {
        long capacity = properties.hashAmountToLocalCache();
        return new ArrayBlockingQueue<>((int) capacity);
    }
}
