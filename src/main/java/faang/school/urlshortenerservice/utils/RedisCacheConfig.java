package faang.school.urlshortenerservice.utils;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.Collections;

@Configuration
@EnableRedisRepositories(keyspaceConfiguration = RedisCacheConfig.MyKeyspaceConfiguration.class)
public class RedisCacheConfig {

    @Value("${spring.data.redis.live-time-cache}")
    private static long liveTimeCache;

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {
        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(Session.class, "session");
            keyspaceSettings.setTimeToLive(liveTimeCache);
            return Collections.singleton(keyspaceSettings);
        }
    }
}