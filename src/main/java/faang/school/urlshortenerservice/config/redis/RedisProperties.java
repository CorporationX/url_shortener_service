package faang.school.urlshortenerservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("spring.data.redis")
public record RedisProperties(
        String host,
        int port
) {

}