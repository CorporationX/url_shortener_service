package faang.school.urlshortenerservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import faang.school.urlshortenerservice.config.MainConfig;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {
    private final MainConfig mainConfig;

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(mainConfig.getTtlMins()))
            .disableCachingNullValues();
        
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build();

        return redisCacheManager;
    }
}
