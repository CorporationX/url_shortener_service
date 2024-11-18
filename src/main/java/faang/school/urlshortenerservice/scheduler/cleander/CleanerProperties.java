package faang.school.urlshortenerservice.scheduler.cleander;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.hash.scheduler.cleaner")
public class CleanerProperties {

    private int lifeTimeDays;
    private int batchSize;
}
