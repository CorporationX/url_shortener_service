package faang.school.urlshortenerservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
