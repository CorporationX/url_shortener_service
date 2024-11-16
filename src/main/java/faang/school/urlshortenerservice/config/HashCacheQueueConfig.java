package faang.school.urlshortenerservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@RequiredArgsConstructor
public class HashCacheQueueConfig {

    @Value("${hash-cache.max-cache-size}")
    private final int maxCacheSize;

    @Bean
    public BlockingQueue<String> queueHash() {
        return new LinkedBlockingQueue<>(maxCacheSize);
    }
}
