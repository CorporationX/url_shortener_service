package faang.school.urlshortenerservice.config.properties.url;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "shortener.url")
public record UrlProperties(
        @NotNull URI domain
) {
}
