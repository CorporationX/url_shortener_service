package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "scheduler.cleaner")
public class SchedulerProperties {
    private String cron;
    private int lifetimeYears = 1;
}