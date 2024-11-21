package faang.school.urlshortenerservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties
public class RedisProperties {

    private String host;
    private int port;
    private int urlTtl;
}
