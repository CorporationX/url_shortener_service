package faang.school.urlshortenerservice.config.properties.hash;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "shortener.hash.generator")
public record HashGeneratorProperties(
        @Positive int batchSize
) {
}
