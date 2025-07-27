package faang.school.urlshortenerservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.redis")
public record RedisProperties(
        String host,
        int port
) {

}