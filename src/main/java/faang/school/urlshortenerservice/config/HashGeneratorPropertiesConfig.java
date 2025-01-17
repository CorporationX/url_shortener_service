package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash-generator")
public record HashGeneratorPropertiesConfig(long batchSize) {
}
