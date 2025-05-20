package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "thread-pool")
@Component
@Data
public class ThreadPoolsProperties {
    private Sizes sizes;

    @Data
    public static class Sizes {
        private int saveToCache;
    }
}
