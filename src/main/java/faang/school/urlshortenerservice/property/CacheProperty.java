package faang.school.urlshortenerservice.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "async.hash-cache-fill")
@Component
public class CacheProperty {
    private String threadName;
    private int poolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
