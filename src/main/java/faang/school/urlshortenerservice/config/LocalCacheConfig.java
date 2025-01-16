package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class LocalCacheConfig {

    @Value("${local-cache.capacity}")
    private int localCacheCapacity;

    @Bean
    public ArrayBlockingQueue<Hash> localCache() {
        return new ArrayBlockingQueue<>(localCacheCapacity);
    }
}
