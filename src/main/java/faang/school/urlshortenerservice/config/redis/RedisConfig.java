package faang.school.urlshortenerservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Bean
    RedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public StringRedisTemplate redisCacheTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
