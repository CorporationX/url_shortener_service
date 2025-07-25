package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "constants")
public class ConstantsProperties {
    private int generationBathSize;
    private int localHashCacheButchSize;
    private int generationThresholdPercent;
    private int cleanUpBatchSize;
    private String expirationInterval;
}
