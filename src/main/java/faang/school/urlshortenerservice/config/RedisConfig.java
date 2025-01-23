package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Конфигурационный класс для настройки подключения к Redis.
 * Создает бины для работы с Redis: фабрику подключений, RedisTemplate и менеджер кэша.
 */
@Configuration
public class RedisConfig {

    /**
     * Хост Redis. Задается через конфигурацию (application.properties).
     */
    @Value("${spring.data.redis.host}")
    private String redisHost;

    /**
     * Порт Redis. Задается через конфигурацию (application.properties).
     */
    @Value("${spring.data.redis.port}")
    private int redisPort;

    /**
     * Создает и возвращает фабрику подключений к Redis.
     *
     * @return Фабрика подключений к Redis.
     */
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    /**
     * Создает и возвращает RedisTemplate для работы с Redis.
     * Настраивает сериализацию ключей и значений в строковый формат.
     *
     * @return RedisTemplate для работы с Redis.
     */
    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * Создает и возвращает менеджер кэша для Redis.
     *
     * @return Менеджер кэша для Redis.
     */
    @Bean
    public RedisCacheManager cacheManager() {
        return RedisCacheManager.builder(jedisConnectionFactory()).build();
    }
}
