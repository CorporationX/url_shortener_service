package faang.school.urlshortenerservice.config.propertis.hash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hash.threads")
public class ThreadProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String threadName;
}
