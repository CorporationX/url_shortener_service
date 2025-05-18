package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "retry")
public class RetryProperties {

        @NotNull
        private Integer maxAttempts;

        @NotNull
        private Long initialDelay;

        @NotNull
        private Long maxDelay;

        @NotNull
        private Double multiplier;
}

