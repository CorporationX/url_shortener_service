package faang.school.urlshortenerservice.config.context.scheduler.hashgenerator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduling.hash-generation")
@Getter
@Setter
public class HashGeneratorScheduleProperties {

    private String cron;
    private boolean enabled;
}
