package faang.school.urlshortenerservice.config.properties.hash;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "shortener.hash.cache")
public record HashCacheProperties(
        @Positive int capacity,
        @Min(1) @Max(99) int minLimitPercent
) {
}
