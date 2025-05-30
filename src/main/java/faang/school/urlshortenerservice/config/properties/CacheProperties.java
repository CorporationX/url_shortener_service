package faang.school.urlshortenerservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash.cache")
public record CacheProperties (Integer maxSize, Integer refillPercent, Integer corePool, Integer capacity, Long keepAlive) {
}
