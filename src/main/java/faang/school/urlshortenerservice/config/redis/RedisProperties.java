package faang.school.urlshortenerservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties("spring.data.redis")
@Data
@Component
public class RedisProperties {
    public String host;
    public int port;
}
