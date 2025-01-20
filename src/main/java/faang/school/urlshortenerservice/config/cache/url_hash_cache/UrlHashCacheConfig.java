package faang.school.urlshortenerservice.config.cache.url_hash_cache;

import faang.school.urlshortenerservice.interceptor.UrlCachingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class UrlHashCacheConfig {

    @Value("${short-url.cache.ttl-minutes}")
    private int ttlMinutes;

    @Bean(name = "urlHashCacheManager")
    public CacheManager cacheManager(JedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(ttlMinutes))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .transactionAware()
                .build();
    }

    @Bean
    public DefaultPointcutAdvisor urlHashCachingAdvisor(UrlCachingInterceptor urlHashCachingInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("""
                        execution(@org.springframework.cache.annotation.Cacheable 
                        * faang.school.urlshortenerservice.service.UrlService.getOriginalUrl(String))
                        """);
        return new DefaultPointcutAdvisor(pointcut, urlHashCachingInterceptor);
    }
}