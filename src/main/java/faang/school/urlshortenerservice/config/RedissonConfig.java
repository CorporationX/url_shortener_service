package faang.school.urlshortenerservice.config;

import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedissonConfig {
    private String host;
    private String port;

    @Value("${redisson.filling-flag-key}")
    private String fillingFlagKey;

    @Value("${redisson.filling-lock-key}")
    private String fillingLockKey;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://%s:%s".formatted(host, port));
        return Redisson.create(config);
    }

    @Bean
    public RAtomicLong fillingFlag(RedissonClient redissonClient) {
        return redissonClient.getAtomicLong(fillingFlagKey);
    }

    @Bean
    public RLock fillingLock(RedissonClient redissonClient) {
        return redissonClient.getLock(fillingLockKey);
    }
}
