package faang.school.urlshortenerservice.config.threads;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task.execution.pool")
public record ThreadPoolProperties(
        int size,
        int maxSize,
        String prefix
) {
}