package faang.school.urlshortenerservice.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "data.redis")
public record RedisProperties(String host, int port) {
}
