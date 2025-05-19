package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "url-shortener.cache")
public class CacheProperties{

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    @Min(1)
    private Integer maxPercentage;

    @NotNull
    @Min(1)
    private Integer fillPercentage;

    @NotNull
    @Min(1)
    private Integer timeout;
}

