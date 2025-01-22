package faang.school.urlshortenerservice.config.cache;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    @Bean
    CacheManager redisCacheManager(CacheProperties cacheProperties, RedisConnectionFactory jedisConnectionFactory) {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig();
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        cacheProperties.getNames().forEach((name, props) ->
                configs.put(name, RedisCacheConfiguration.defaultCacheConfig().entryTtl(props.getExpire())));

        return RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
