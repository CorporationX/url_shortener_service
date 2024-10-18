package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public Jedis jedis(@Value("${spring.data.redis.host}") String host,
                       @Value("${spring.data.redis.port}") int port) {
        return new Jedis(host, port);
    }

}
