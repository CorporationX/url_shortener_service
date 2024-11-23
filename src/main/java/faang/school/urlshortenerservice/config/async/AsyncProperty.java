package faang.school.urlshortenerservice.config.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.async")
public class AsyncProperty {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
