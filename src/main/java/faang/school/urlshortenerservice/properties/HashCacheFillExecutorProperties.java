package faang.school.urlshortenerservice.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "async.filler")
public class HashCacheFillExecutorProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
