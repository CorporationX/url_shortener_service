package faang.school.urlshortenerservice.config.cache;

import faang.school.urlshortenerservice.properties.short_url.ShortUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final JedisConnectionFactory jedisConnectionFactory;
    private final ShortUrlProperties shortUrlProperties;

    @Bean
    public CacheManager redisCacheManager() {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(shortUrlProperties.getCacheSettings().getDefaultTtlMinutes()))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "myCache::".concat(cacheName));

        return RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}