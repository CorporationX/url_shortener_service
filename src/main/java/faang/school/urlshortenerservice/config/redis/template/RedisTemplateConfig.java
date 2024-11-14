package faang.school.urlshortenerservice.config.redis.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
public class RedisTemplateConfig {
    @Bean
    public RedisTemplate<String, Url> urlRedisTemplate(JedisConnectionFactory jedisConnectionFactory,
                                                       ObjectMapper javaTimeModuleObjectMapper,
                                                       StringRedisSerializer stringRedisSerializer) {
        return buildRedisTemplate(jedisConnectionFactory, javaTimeModuleObjectMapper, Url.class, stringRedisSerializer);
    }

    @SuppressWarnings("SameParameterValue")
    private <T> RedisTemplate<String, T> buildRedisTemplate(RedisConnectionFactory connectionFactory,
                                                            ObjectMapper objectMapper, Class<T> clazz,
                                                            StringRedisSerializer stringRedisSerializer) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);

        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
}
