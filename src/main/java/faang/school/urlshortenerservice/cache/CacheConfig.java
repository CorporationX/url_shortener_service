package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {
    private final HashCacheProperties hashCacheProperties;
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put(hashCacheProperties.getKeyPrefix(), RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(hashCacheProperties.getTtl()))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                ));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}
