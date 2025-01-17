package faang.school.urlshortenerservice.config.redis;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties("spring.data.redis")
public record RedisPropertiesConfig(Integer timeToLiveInMinutes) {
}
