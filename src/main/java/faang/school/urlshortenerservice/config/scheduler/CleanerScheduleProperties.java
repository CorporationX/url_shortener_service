package faang.school.urlshortenerservice.config.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduling.url-cleaner")
@Getter
@Setter
public class CleanerScheduleProperties {

    private String cron;
    private boolean enabled;
}