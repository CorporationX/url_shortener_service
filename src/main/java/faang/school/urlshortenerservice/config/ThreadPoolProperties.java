package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolProperties {
    private int poolSize;
    private int queueCapacity;
}
