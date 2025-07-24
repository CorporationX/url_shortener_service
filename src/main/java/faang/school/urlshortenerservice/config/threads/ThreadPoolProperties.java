package faang.school.urlshortenerservice.config.threads;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task.execution.pool")
public record ThreadPoolProperties(
        int coreSize,
        int maxSize,
        String prefix
) {
}