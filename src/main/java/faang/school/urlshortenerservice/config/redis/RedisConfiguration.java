package faang.school.urlshortenerservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.entity.url.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(redisProperties.getUrlTtl()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.
                        fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Url> redisUrlTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Url> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        Jackson2JsonRedisSerializer<Url> serializer = new Jackson2JsonRedisSerializer<>(Url.class);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());

        return template;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        redisCacheConfiguration.usePrefix();

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(factory)
                .cacheDefaults(redisCacheConfiguration).build();
    }
}
