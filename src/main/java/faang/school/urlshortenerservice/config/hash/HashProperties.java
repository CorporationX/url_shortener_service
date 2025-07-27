package faang.school.urlshortenerservice.config.hash;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("hash.config")
public class HashProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String prefix;
}
