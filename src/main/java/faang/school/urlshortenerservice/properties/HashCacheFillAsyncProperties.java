package faang.school.urlshortenerservice.properties;

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
public class HashCacheFillAsyncProperties {
    private String threadNamePrefix;
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
