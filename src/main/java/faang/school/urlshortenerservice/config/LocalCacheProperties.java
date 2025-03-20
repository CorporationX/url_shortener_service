package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "hash.local-cache")
public class LocalCacheProperties {
    private int capacity;
    private int fillPercentage;
}
