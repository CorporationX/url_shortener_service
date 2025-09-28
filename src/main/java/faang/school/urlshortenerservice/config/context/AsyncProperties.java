package faang.school.urlshortenerservice.config.context;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "async.hash-generator")
public class AsyncProperties {
    @Min(2000)
    private int threadPoolSize;
    @Min(10000)
    private int threadPoolQueueCapacity;
}
