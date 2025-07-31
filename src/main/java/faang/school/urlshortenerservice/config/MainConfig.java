package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class MainConfig {
    @Value("${hashes.numberOfNumbers}")
    private int numberOfNumbers;

    @Value("${data.redis.ttlMins}")
    private int ttlMins;

    @Value("${hashes.numberOfHashes}")
    private int numberOfHashes;

    @Value("${hashes.mimThresholdPercent}")
    private int mimThresholdPercent;

    @Value("${async.corePoolSize}")
    private int corePoolSize;

    @Value("${async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.queueCapacity}")
    private int queueCapacity;

    @Value("${async.threadNamePrefix}")
    private String threadNamePrefix;
}
