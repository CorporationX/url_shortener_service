package faang.school.urlshortenerservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration

public class RedisCacheConfig {

    @Value("${cache.redis.host}")
    private String host;
    @Value("${cache.redis.port}")
    private int port;

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

//@Bean
//    public RedisCacheConfiguration cacheConfiguration() {
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .disableCachingNullValues()
//                .entryTtl(Duration.ofDays(2));
//    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration factory = new RedisStandaloneConfiguration();
        factory.setHostName(host);
        factory.setPort(port);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(factory);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

//    @Bean
//    public RedisCacheManager cacheManager() {
//    return RedisCacheManager.builder(jedisConnectionFactory()).cacheDefaults(cacheConfiguration()).build();
//    }
}
