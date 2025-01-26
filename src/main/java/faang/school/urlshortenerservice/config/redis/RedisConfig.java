package faang.school.urlshortenerservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.topics.url_by_hash}")
    private String topicUrlByHash;

    @Bean
    public JedisConnectionFactory configuration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(configuration);
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    @Bean
    public RedisTemplate<String, Url> defaultRedisTemplate() {
        RedisTemplate<String, Url> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(configuration());
        return redisTemplate;
    }
    @Bean
    public BoundHashOperations<String, String, String> urlByHashOps(RedisTemplate<String, Url> defaultRedisTemplate) {
        return defaultRedisTemplate.boundHashOps(topicUrlByHash);
    }
}
