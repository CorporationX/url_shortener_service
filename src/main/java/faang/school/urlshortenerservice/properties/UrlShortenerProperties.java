package faang.school.urlshortenerservice.properties;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "url-shortener-service")
public class UrlShortenerProperties {
    private int batchSize;
    private ExecutorProperties hashGeneratorThreadPool;
    private ExecutorProperties executorService;
    private int minimumHashLengthInPercent;

    @Data
    @Validated
    public static class ExecutorProperties {
        @Min(1)
        private int corePoolSize;
        @Min(1)
        private int maxPoolSize;
        @Min(1)
        private int queueCapacity;
        private int keepAliveTime;
    }
}
