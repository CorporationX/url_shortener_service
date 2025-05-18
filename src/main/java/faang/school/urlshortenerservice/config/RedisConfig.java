package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final RedisProperties properties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        log.info("Creating Redis connection factory for {}:{}", properties.getHost(), properties.getPort());
        RedisStandaloneConfiguration standalone =
                new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(properties.getPool().getMaxTotal());
        poolConfig.setMaxIdle(properties.getPool().getMaxIdle());
        poolConfig.setMinIdle(properties.getPool().getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(properties.getPool().getMaxWait()));

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .and()
                .connectTimeout(Duration.ofMillis(properties.getTimeout()))
                .readTimeout(Duration.ofMillis(properties.getTimeout()))
                .build();

        return new JedisConnectionFactory(standalone, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setConnectionFactory(connectionFactory);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setEnableTransactionSupport(true);
        return template;
    }
}
