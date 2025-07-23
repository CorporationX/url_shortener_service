package faang.school.urlshortenerservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("spring.data.redis")
public class RedisProperties {
    public String host;
    public int port;
}
