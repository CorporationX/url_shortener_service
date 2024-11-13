package faang.school.urlshortenerservice.config.async;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "server.thread.pool")
public class AsyncThreadPoolProperties {
    @Min(1)
    private int corePoolSize;

    @Min(1)
    private int maximumPoolSize;

    @Min(0)
    private long keepAliveTime;

    @Min(1)
    private int queueCapacity;
}
