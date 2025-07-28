package faang.school.urlshortenerservice.config.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.executor.filling-memory-cache")
@Configuration
public class FillingMemoryCacheExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String prefix;
}
