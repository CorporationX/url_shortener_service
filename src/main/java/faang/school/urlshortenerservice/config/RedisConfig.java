package faang.school.urlshortenerservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RedisConfig — базовая конфигурация для redis.
 *
 * @author bozya
 * @since 19.09.2025
 */
@Configuration
public class RedisConfig {
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}