package faang.school.urlshortenerservice.config.properties.url;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "shortener.cleaner")
public record CleanerProperties(
        @NotBlank String cron,
        @DurationMin(seconds = 0) Duration retention
) {
    @AssertTrue(message = "Invalid cron expression")
    public boolean isCronValid() {
        return CronExpression.isValidExpression(cron);
    }
}
