package faang.school.urlshortenerservice.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash.thread-pool")
public record HashThreadPoolProperties(
        int coreSize,
        int maxSize,
        int queueCapacity
) {
}
