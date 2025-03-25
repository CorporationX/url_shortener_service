package faang.school.urlshortenerservice.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.async")
public record AsyncProperties(

        int corePoolSize,

        int maxPoolSize,

        int queueCapacity,

        String threadNamePrefix
) {
}
