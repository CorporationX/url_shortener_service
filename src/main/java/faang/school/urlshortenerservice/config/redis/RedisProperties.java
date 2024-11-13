package faang.school.urlshortenerservice.config.redis;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private Long ttl;
}
