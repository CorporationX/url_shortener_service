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
        return new JedisConnectionFactory(
                createRedisStandaloneConfiguration(),
                createJedisClientConfiguration());
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

    private RedisStandaloneConfiguration createRedisStandaloneConfiguration() {
        String host = properties.getHost();
        Integer port = properties.getPort();

        log.info("Creating Redis connection factory for {}:{}", host, port);

        return new RedisStandaloneConfiguration(host, port);
    }

    private JedisClientConfiguration createJedisClientConfiguration() {
        Long timeout = properties.getTimeout();

        return JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(createJedisPoolConfig())
                .and()
                .connectTimeout(Duration.ofMillis(timeout))
                .readTimeout(Duration.ofMillis(timeout))
                .build();
    }

    private JedisPoolConfig createJedisPoolConfig() {
        RedisProperties.Pool pool = properties.getPool();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(pool.getMaxTotal());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(pool.getMaxWait()));

        return poolConfig;
    }
}
