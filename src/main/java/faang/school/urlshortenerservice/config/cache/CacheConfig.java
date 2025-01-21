package faang.school.urlshortenerservice.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheProperties cacheProperties;

    public final static String URL_CACHE_NAME = "urls";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        CacheProperties.Cache cacheProps = cacheProperties.getUrlCache();

        Map<String, RedisCacheConfiguration> config = new HashMap<>();

        config.put(cacheProps.getPrefix(),
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(cacheProps.getTtlInMinutes()))
        );

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(config)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues())
                .build();
    }
}
