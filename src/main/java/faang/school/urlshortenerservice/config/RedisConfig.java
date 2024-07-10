package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;


@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        var jedisClientConfig = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        return new JedisConnectionFactory(jedisClientConfig);
    }

    @Bean
    public RedisTemplate<Hash, Url> redisTemplate() {
        var redisTemplate = new RedisTemplate<Hash, Url>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        RedisSerializationContext.SerializationPair<Object> valueSerializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(redisProperties.getUrlTtl()))
                .disableCachingNullValues()
                .serializeValuesWith(valueSerializationPair);
    }
}