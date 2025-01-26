package faang.school.urlshortenerservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableJpaRepositories(basePackages = "faang.school.urlshortenerservice.repository.jpa")
@EnableRedisRepositories(basePackages = "faang.school.urlshortenerservice.repository.redis")
public class AppConfig {
    @Value("${spring.properties.thread-pool-size}")
    private int THREAD_POOL_SIZE;

    @Value("${spring.properties.cache-pros.initial-capacity}")
    private int initialCapacity;

    @Value("${spring.properties.cache-pros.maximum-capacity}")
    private int maximumCapacity;

    @Value("${spring.properties.cache-pros.expire}")
    private int expireAfterWrite;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public ExecutorService customThreadPool() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("hashes");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumCapacity)
                .expireAfterWrite(expireAfterWrite, TimeUnit.DAYS));
        return cacheManager;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedis = new JedisConnectionFactory();
        jedis.setHostName(redisHost);
        jedis.setPort(redisPort);
        return jedis;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
