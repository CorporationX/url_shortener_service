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
@ConfigurationProperties(prefix = "hash.cash")
public class HashCacheQueueProperties {

    private int maxQueueSize;
    private int percentageToStartFill;
    private int fillingBatchesQuantity;
    private int countToStopGenerate;
}
