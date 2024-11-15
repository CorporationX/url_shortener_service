package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("server.hash.clear")
public class ClearProperties {
    private int batchSize;
    private int daysThreshold;
}
