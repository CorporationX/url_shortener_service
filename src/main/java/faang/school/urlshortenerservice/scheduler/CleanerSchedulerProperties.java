package faang.school.urlshortenerservice.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "scheduler.properties")
public class CleanerSchedulerProperties {
    private String lockName;
    private String lockAtMostFor;
    private String lockAtLeastFor;
}
