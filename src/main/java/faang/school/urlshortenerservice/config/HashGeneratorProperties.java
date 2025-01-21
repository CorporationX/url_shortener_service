package faang.school.urlshortenerservice.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "hash-generator")
public record HashGeneratorProperties(long hashBatchSize) {
}
