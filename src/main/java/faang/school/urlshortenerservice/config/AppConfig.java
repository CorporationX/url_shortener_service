package faang.school.urlshortenerservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {
    @Value("${spring.properties.thread-pool-size}")
    private int THREAD_POOL_SIZE;

    @Value("${spring.properties.cache-pros.initial-capacity}")
    private int initialCapacity;

    @Value("${spring.properties.cache-pros.maximum-capacity}")
    private int maximumCapacity;

    @Value("${spring.properties.cache-pros.expire}")
    private int expireAfterWrite;

    @Value("${spring.properties.cache-pros.ttl}")
    private int ttl;

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

//    @Bean
//    public RedisCacheConfiguration cacheConfiguration() {
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(ttl))
//                .disableCachingNullValues()
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
//                );
//    }
//
//    @Bean
//    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//        return (builder) -> builder
//                .withCacheConfiguration("redisHashes",
//                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(ttl))
//                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
//                       new GenericJackson2JsonRedisSerializer()))
//                );
//
//    }
}
