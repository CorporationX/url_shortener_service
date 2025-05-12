package faang.school.urlshortenerservice.config.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(String host,
                              Integer port
) {
}
