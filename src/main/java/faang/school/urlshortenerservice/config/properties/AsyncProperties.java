package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "thread-pool")
public class AsyncProperties {

        @NotNull
        @Min(1)
        private Integer corePoolSize;

        @NotNull
        @Min(1)
        private Integer maxPoolSize;

        @NotNull
        @Min(0)
        private Integer queueCapacity;

        @NotNull
        @Min(1)
        private Integer keepAliveSeconds;

        @NotBlank
        private String threadNamePrefix;

        @NotNull
        private Boolean allowCoreThreadTimeout;

        @NotNull
        private Boolean waitToCompleteOnShutdown;

        @NotNull
        private Integer awaitTerminationSeconds;
}

