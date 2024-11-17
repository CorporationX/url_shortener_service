package faang.school.urlshortenerservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Url> urlRedisTemplate(JedisConnectionFactory connectionFactory,
                            ObjectMapper objectMapper,
                            StringRedisSerializer stringRedisSerializer) {
        RedisTemplate<String, Url> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);

        Jackson2JsonRedisSerializer<Url> serializer
                = new Jackson2JsonRedisSerializer<>(objectMapper, Url.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
}
