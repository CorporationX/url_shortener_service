package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "generate.pool")
public class SchedulerPoolProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
