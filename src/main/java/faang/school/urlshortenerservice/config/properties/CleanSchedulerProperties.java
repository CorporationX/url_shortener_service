package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "clean-scheduler")
public class CleanSchedulerProperties {

        @NotBlank
        private String cron;

        @NotNull
        @Min(1)
        private Integer expirationDays;
}

