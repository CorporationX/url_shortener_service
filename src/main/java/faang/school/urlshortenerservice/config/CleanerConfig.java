package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.cleaner")
public class CleanerConfig {

    private int deleteOlderThanDays;
    private String cron;
    private boolean enabled;
}
