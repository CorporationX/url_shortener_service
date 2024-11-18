package faang.school.urlshortenerservice.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hash.cache")
public class CacheProperties {
    private int size;
    private int percentOfSize;
}
