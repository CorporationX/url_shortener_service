package faang.school.urlshortenerservice.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "async.hash-cache-fill")
public class CacheProperty {
    private String threadName;
    private int poolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
