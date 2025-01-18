package faang.school.urlshortenerservice.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash.generator.async")
public record AsyncProperties(
        int threadPoolSize,
        int threadQueueCapacity,
        String threadNamePrefix
) {
}
