package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hash")
public class HashProperties {
    private Cache cache;
    private Integer generationBatch;
    private Integer saveBatch;
    private Integer getBatch;

    @Data
    public static class Cache {
        private Integer capacity;
        private Integer lowSizePercentage;
    }
}