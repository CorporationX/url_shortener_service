package faang.school.urlshortenerservice.config.scheduler;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "scheduling.url-cleaner")
public class CleanerScheduleProperties {

    @NotBlank(message = "Cron expression must not be blank")
    private String cron;
    
    private boolean enabled;
}