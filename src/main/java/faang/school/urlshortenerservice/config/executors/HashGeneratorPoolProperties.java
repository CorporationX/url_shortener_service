package faang.school.urlshortenerservice.config.executors;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("executors.hash-generator")
public record HashGeneratorPoolProperties(
        int poolSize,
        int queueCapacity,
        int awaitSeconds
) {
}
