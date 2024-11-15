package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("server.hash.fetch")
public class FetchProperties {
    private int batchSize;
    private int limitOnReplenishment;
}
