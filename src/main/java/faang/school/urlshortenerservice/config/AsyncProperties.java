package faang.school.urlshortenerservice.config;

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
    @Min(1)
    private int threadPoolSize;
    @Min(1)
    private int threadPoolQueueCapacity;
}
