package faang.school.urlshortenerservice.config.properties.url;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Validated
@ConfigurationProperties(prefix = "shortener.cache")
public record UrlCacheProperties(
        @NotBlank String prefix,
        @DurationUnit(ChronoUnit.SECONDS) Duration ttl
) {
}
