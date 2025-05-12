package faang.school.urlshortenerservice.config.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cache.redis")
public record RedisCacheProperties(String name,
                                   Duration timeToLive
) {
}
