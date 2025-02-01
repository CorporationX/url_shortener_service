package faang.school.urlshortenerservice.config.scheduler;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SchedulerConfig {

    @Value("${hash.scheduled}")
    private String cronHistoryTime;

}