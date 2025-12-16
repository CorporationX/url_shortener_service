package faang.school.urlshortenerservice.config.hash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class UrlShortenerConfig {
    @Value("${hash.insert-batch-size}")
    private int insertBatchSize;

    @Value("${hash.delete-batch-size}")
    private int deleteBatchSize;

    @Value("${base62.chars}")
    private String base62Chars;

    @Value("${numbers.count}")
    private int numberCount;

    @Value("${executor.pool.size}")
    private int poolSize;

    @Value("${executor.queue.size}")
    private int queueSize;

    @Value("${executor.prefix}")
    private String executorPrefix;

    @Value("${cache.size}")
    private int cacheSize;

    @Value("${cache.refillPercent}")
    private float refillPercent;

    @Value("${cache.attemptsCount}")
    private int attemptsCount;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${hash.cache.key}")
    private String hashCacheKey;

    @Value("${url.cache.key}")
    private String urlCacheKey;

    @Value("${hash.cache.ttl}")
    private long cacheTtl;

    @Value("${url.prefix}")
    private String urlPrefix;
}
