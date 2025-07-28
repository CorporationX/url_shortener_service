package faang.school.urlshortenerservice.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("hash.properties")
public class HashCacheProperties {
    private int minPercent;
    private int maxCacheSize;
}
