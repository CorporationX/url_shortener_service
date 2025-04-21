package faang.school.urlshortenerservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "url-shortener-properties")
public class UrlShortenerProperties {

    private int lowerBoundPercentageFill;
    private ExecutorService executorService;
    private int batchSizeMax;
    private Duration hashedUrlLifetime;


    @Data
    public static class ExecutorService {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private int keepAliveTime;
    }
}
